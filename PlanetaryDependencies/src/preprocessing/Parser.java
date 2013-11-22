package preprocessing;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import control.Main;

public class Parser {
	ArrayList<ClassPacket> parsedList = new ArrayList<ClassPacket>();
	ArrayList<String> classList = new ArrayList<String>();
	
	/**
	 * Parses file and returns a list of classPackets to be used in analyzer
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 *ArrayList<ClassPacket>
	 */
	public ArrayList<ClassPacket> parse(File file) throws FileNotFoundException{
		System.out.println("Parser started.");


		// Complete parsing method here
		//from project, find the src folder containing packages
		System.out.println("Folder selected: "+file.getAbsolutePath());

		File src = new File(file.getAbsolutePath());
		for(File folder : src.listFiles()){
			if(folder.getName().equals("src")&&folder.isDirectory()){
				src = folder;
			}
		}
		System.out.println(src.getAbsolutePath());

		//first run to determine the list of everything.
		scanFolder(src);

		//second run to parse data
		parseFolder(src);
		return parsedList;

	}

/**
 * Looks for folders with Java files
 * @param file
 *void
 */
	private void scanFolder(File file){
		if(!file.isDirectory()&&file.getName().endsWith(".java")){
			String className = file.getName().split("\\.")[0];
			if(!classList.contains(className))
                classList.add(className);
			return;
		}
		else if(file.isDirectory()){
			for(File folder : file.listFiles()){
				scanFolder(folder);
			}
		}
	}

	/**
	 * Gets all the files in the folder ending in .java and parses those
	 * @param file
	 *void
	 */
	private void parseFolder(File file){
		if(!file.isDirectory()&&file.getName().endsWith(".java")){
			parseJavaFile(file);
			return;
		}
		else if(file.isDirectory()){
			for(File folder : file.listFiles()){
				parseFolder(folder);
			}
		}
	}
	
	/**
	 * Parses files in order to find associations to be analyzed in analyzer
	 * @param javaClassFile, file to be parsed
	 *void
	 */
	private void parseJavaFile(File javaClassFile) {
		try {
			CompilationUnit cu = JavaParser.parse(javaClassFile);
			ClassPacket cp = 
					new ClassPacket(javaClassFile.getName().split("\\.")[0], 
							cu.getPackage().getName().toString(), 
							cu.getEndLine());
			VariableVisitor vv = new VariableVisitor();
			List<TypeDeclaration> f_vars = cu.getTypes();
			for (TypeDeclaration type : f_vars)
			{
				List<BodyDeclaration> members = type.getMembers();
				for (BodyDeclaration member : members)
				{
					if (member instanceof FieldDeclaration)
					{
						FieldDeclaration myType = (FieldDeclaration) member;
						if(existsInList(myType.getType().toString(),classList)){
							vv.instantiated.add(myType.getType().toString());
						}
					}
				}
			}
			vv.visit(cu, null);
			
			//instantiated Adding and association adding
			for(String s : vv.instantiated){
				if(existsInList(s, classList)){
					cp.addToInstantiated(s);
				}
				else if(s.contains("<")&&s.contains(">")){
					int start = s.indexOf('<');
					int end = s.indexOf('>');
					String collection = s.substring(0, start);
					String type = s.substring(start+1, end);
					if(existsInList(type, classList)){
						cp.addToAssociatedWith(type);
					}
					else if(existsInList(collection, classList)){
						cp.addToAssociatedWith(collection);

					}
				}
			};
			for(String s : vv.assignment){
				if(existsInList(s, classList)){
					cp.addToAssigned(s);
				}
				else if(s.contains("<")&&s.contains(">")){
					int start = s.indexOf('<');
					int end = s.indexOf('>');
					String collection = s.substring(0, start);
					String type = s.substring(start+1, end);
					if(existsInList(type, classList)){
						cp.addToAssociatedWith(type);
					}
					else if(existsInList(collection, classList)){
						cp.addToAssociatedWith(collection);

					}
				}
			};
			//more associations
			MethodVisitor mv = new MethodVisitor();
			mv.visit(cu, null);
			for(String s : mv.methodsParams){
				if(existsInList(s, classList)){
					cp.addToAssociatedWith(s);
				}
				else if(s.contains("<")&&s.contains(">")){
					int start = s.indexOf('<');
					int end = s.indexOf('>');
					String collection = s.substring(0, start);
					String type = s.substring(start+1, end);
					if(existsInList(type, classList)){
						cp.addToAssociatedWith(type);
					}
					else if(existsInList(collection, classList)){
						cp.addToAssociatedWith(collection);

					}
				}
			};
			//static access
			for(TypeDeclaration type : cu.getTypes()){
				List<BodyDeclaration> bodyList = type.getMembers();
				for(BodyDeclaration bd : bodyList){
					for(String cName : classList){
						if(bd.toString().matches("(?s).*\\b"+cName+"\\.[[a-zA-Z0-9]*].*")){
							if(bd.toString().matches("(?s).*\\b"+cName+"\\.class.*")){
								cp.addToAssociatedWith(cName);
							}
							
							//System.out.println("Static Access:"+cName);
							else
								cp.addToStaticAccess(cName);
						}
					}
				}
			}
			
			
			parsedList.add(cp);

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class VariableVisitor extends VoidVisitorAdapter{
		ArrayList<String> instantiated = new ArrayList<String>();
		ArrayList<String> assignment = new ArrayList<String>();
		@Override
		public void visit(VariableDeclarationExpr n, Object arg)
		{  
				instantiated.add(n.getType().toString());
		}
	}
	
	public class MethodVisitor extends VoidVisitorAdapter {
		 ArrayList<String> methodsParams = new ArrayList<String>();
		 ArrayList<String> methods = new ArrayList<String>();

	        public void visit(MethodDeclaration n, Object arg)
	        {
	        	List<Parameter> params = n.getParameters();
	        	if(params != null){
	        		for(Parameter p : params){
	        			methodsParams.add(p.getType().toString());
	        		}
	        	}
	        }
	  }
	 
	 /**
	  * Checks if name of class already exists in the list, returns true if it is.
	  * @param name
	  * @param classList
	  * @return
	  *boolean
	  */
	private static boolean existsInList(String name, ArrayList<String> classList) {
		for(String s : classList){
			if(name.equals(s))
                return true;
		}
		return false;
	}


}
