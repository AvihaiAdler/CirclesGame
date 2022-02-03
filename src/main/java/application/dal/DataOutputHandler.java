package application.dal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Stream;

import org.tinylog.Logger;

public class DataOutputHandler {
	private final String fileName;
	private final boolean hasTitle;
	private final BufferedWriter writer;

	public DataOutputHandler(String fileName) {
		this.fileName = fileName;
		this.hasTitle = hasTitle();

		try {
			if (!createDir())
				throw new RuntimeException(
						"Couldn't create directories " + fileName.substring(0, fileName.lastIndexOf('/')));
			this.writer = new BufferedWriter(new FileWriter(fileName, hasTitle));
		} catch (IOException e) {
			throw new RuntimeException(
					"Something went wrong with" + fileName + ". Close all open windows and try again\n" + e);
		}
	}

	private boolean createDir() throws IOException {
		return Stream.of(fileName).map(str -> str.substring(0, str.lastIndexOf('/'))).map(str -> new File(str))
				.anyMatch(file -> {
					if (!file.exists()) {
						Logger.info("Creating new directory [" + file.getName() + "]");
						return file.mkdirs();
					}
					return true;
				});
	}

	/*
	 * check if the file exists. if not: create one. if it does exists: check if it
	 * contains any value: if it does don't write a title
	 */
	private boolean hasTitle() {
		try {
			var reader = new BufferedReader(new FileReader(fileName));
			var line = reader.readLine();
			reader.close();
			if (line != null)
				return true;
			return false;
		} catch (IOException e) {
			Logger.info("File " + fileName + " doesn't exists. Creating a new file instead");
			return false;
		}
	}

	/*
	 * writes a string 'as is' into a file an end line character will be added to
	 * the end of the string. the string must be formatted properly
	 */
	public void writeLine(String data, DataType type) throws IOException {
		writeData(data, type, true);
	}

	/*
	 * writes a string 'as is' into a file. the string must be formatted properly
	 */
	public void write(String data, DataType type) throws IOException {
		writeData(data, type, false);
	}

	private void writeData(String data, DataType type, boolean endLine) throws IOException {
		switch (type) {
		case Title:
			if (hasTitle)
				return;
		case Data:
			Logger.info("Writing data: [" + data + "] to " + fileName);
			writer.write(data);

			if (endLine)
				writer.newLine();
			break;
		}
	}

	public void close() throws IOException {
		writer.flush();
		Logger.info("Closing data stream");
		writer.close();
	}
}
