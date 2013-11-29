package control;

import java.io.File;
import java.util.ArrayList;


import preprocessing.Analyser;
import preprocessing.ClassDependency;
import preprocessing.ClassPacket;
import preprocessing.Parser;
import visualisation.Visualiser;
import javafx.application.Application;
import javafx.stage.Stage;



public class Main extends Application {
	Parser parser = new Parser();
	Visualiser v = new Visualiser();
	public static void main(String[] args) {
		launch(args);
	}


	@Override
	public void start(Stage stage) throws Exception {

        File targetCodeDir = new File(GetTargetCodePath());

		ArrayList<ClassPacket> parserOutput = parser.parse(targetCodeDir);

		ArrayList<ClassDependency> analyserOutput = Analyser.analyse(parserOutput);

		v.process(analyserOutput);

	}

    //Helper, Gets Directory of Code to be Analyzed.
    public static String GetTargetCodePath(){
          File currDir = new File(System.getProperty("user.dir"));
          File parentFile = currDir.getParentFile();
          String targetRelativePath = "\\Target\\Leviathan";
          return parentFile.getAbsolutePath() + targetRelativePath;
    }




}
