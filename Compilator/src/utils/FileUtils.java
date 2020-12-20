package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class FileUtils {
	//Para comprobación de errores
	public static int LOAD_ERROR, SAVE_ERROR = 0;
	public static int LOAD_CORRECT, SAVE_CORRECT = 1;
	public static int FILE_NOT_FOUND = 2;

	private static StringBuilder stringBuffer = null;

	//Abre el archivo y coloca los caracteres leidos en UTF_8 en un buffer de caracteres
	public static int openFile (File file){
		stringBuffer = new StringBuilder();
		Reader reader;
		try {
			reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
			char[] buff = new char[500];
			for (int charsRead; (charsRead = reader.read(buff)) != -1; )
				stringBuffer.append(buff, 0, charsRead);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			return LOAD_ERROR;
		}
		return LOAD_CORRECT;
	}

	//Guarda el contenido de un buffer de caracteres en un archivo
	public static int saveFile(File file, String text) {
		Writer writer;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
			char[] buffer = text.toCharArray();
			for (int charToWrite : buffer) {
				writer.write(charToWrite);
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			return SAVE_ERROR;
		}
		return SAVE_CORRECT;
	}

	//Devuelve el contenido del buffer, si algún archivo fue leído
	public static String getContent() {
		if (stringBuffer != null)
			return stringBuffer.toString();
		else return null;
	}

	public static void reset(){
		stringBuffer = new StringBuilder();
	}
}
