package application.dao;

import java.io.FileNotFoundException;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigureManager {
	private final ObjectMapper jackson;
	private final String fileName;

	public ConfigureManager(String fileName) {
		jackson = new ObjectMapper();
		this.fileName = fileName;
	}

	public Map<String, Object> getProperties() throws FileNotFoundException {
		var json = this.getClass().getClassLoader().getResourceAsStream(fileName);
		try {
			return jackson.readValue(json, new TypeReference<Map<String, Object>>() {});
		} catch (Exception e) {
			throw new FileNotFoundException("The file " + fileName + " couldn't be found");
		}
	}
}
