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

	public ArrayList<ClassPacket> parse(File file) throws FileNotFoundException{
		System.out.println("Parser started.");

		ArrayList<ClassPacket> parsedList = new ArrayList<ClassPacket>();
		ArrayList<String> packageList = new ArrayList<String>();
		ArrayList<String> classList = new ArrayList<String>();
		/*
		 * Complete parsing method here
		 */
		//from project, find the src folder containing packages
		System.out.println("Folder selected: "+file.getAbsolutePath());

		File src = new File(file.getAbsolutePath()+"\\src"); 


		//first run to determine the list of everything.
		for (File packages : src.listFiles()){
			packageList.add(packages.getName());
			for(File javaClassFile : packages.listFiles()){
				if(javaClassFile.getName().endsWith(".java")){
					String javaClassName = javaClassFile.getName().split("\\.")[0];
					classList.add(javaClassName);
				}
			}
		}


		//second run to parse data
		for (File packages : src.listFiles()){
			for(File javaClassFile : packages.listFiles()){
				if(javaClassFile.getName().endsWith(".java")){
					try {
						CompilationUnit cu = JavaParser.parse(javaClassFile);
						ClassPacket cp = 
								new ClassPacket(javaClassFile.getName().split("\\.")[0], 
										cu.getPackage().getName().getName(), 
										cu.getEndLine());
						
						//i think this is no longer necessary
//						//Import adding
//						List<ImportDeclaration> imports = cu.getImports();
//						for(ImportDeclaration i : imports){
//							if(existsInList(i.getName().getName(),classList)){
//								cp.addToDependency(i.getName().getName());
//							}
//						}
						//run visitor
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

						//more associations
						MethodVisitor mv = new MethodVisitor();
						mv.visit(cu, null);
						for(String s : mv.methods){
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
									if(bd.toString().contains(cName+".")){
										//										System.out.println("Static Access:"+cName);
										cp.addToStaticAccess(cName);
									}
								}
							}
						}
						
						
						parsedList.add(cp);

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}

		return parsedList;

	}


	private class VariableVisitor extends VoidVisitorAdapter{
		ArrayList<String> instantiated = new ArrayList<String>();
		@Override
		public void visit(VariableDeclarationExpr n, Object arg)
		{      

			instantiated.add(n.getType().toString());
		}
	}
	
	 public class MethodVisitor extends VoidVisitorAdapter {
		 ArrayList<String> methods = new ArrayList<String>();
	        public void visit(MethodDeclaration n, Object arg)
	        {
	        	List<Parameter> params = n.getParameters();
	        	if(params!=null){
	        		for(Parameter p : params){
	        			methods.add(p.getType().toString());
	        		}
	        	}
	        }
	  }
	 
	private static boolean existsInList(String name, ArrayList<String> classList) {
		for(String s : classList){
			if(name.equals(s))return true;
		}
		return false;
	}


}
