package com.bjit.ewc18x.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JasperReportGenerator {
    private final URL TEMPLATE_FILE_PATH = getClass().getClassLoader().getResource("/templates/BOM-table2.0.jasper");
    private String jsonString;
    
    public String generatePdfReport(String reportName, String requestId) throws Exception {
        int count = 1;
        StringBuilder outputFilePathWithName = new StringBuilder();
        String rawJsonData = getDataFromJSON(getJsonString());
        String reportGeneratePath = "";
        Path reportDirectory = null;
        boolean isReportDirExists = false;
        if (!rawJsonData.isEmpty()) {
            JasperReport report = (JasperReport) JRLoader.loadObject(TEMPLATE_FILE_PATH);
            ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(rawJsonData.getBytes());
            JsonDataSource ds = new JsonDataSource(jsonDataStream);
            Map<String, Object> parameters = new HashMap();
            parameters.put("columnCount", Integer.toString(count++));
            parameters.put("imageLocation", getClass().getClassLoader().getResource("/img/valmet.jpg").toString());
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, ds);
            // Export and save pdf to file
            reportGeneratePath = PropertyReader.getProperty("ebom.reports.folder.path");
            reportDirectory = Paths.get(reportGeneratePath);
            isReportDirExists = Files.exists(reportDirectory);
            if(!isReportDirExists) {
                Files.createDirectories(reportDirectory);
            }
            outputFilePathWithName.append(reportDirectory.toString()).append("/").append(reportName).append("_").append(requestId).append("_report.pdf");
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePathWithName.toString());
            System.out.println("Report is created."); 
        }
        return outputFilePathWithName.toString();
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }
    
    
    
    /*private String getJsonString(String filePath) {
        String data = "";
        JSONParser parser = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(filePath));

            // System.out.println(jsonObject.toJSONString());
            String allData = jsonObject.toJSONString();
            String[] effectiveData = allData.split(":", 2);
            // System.out.println(effectiveData[1]);
            data = effectiveData[1].substring(0, effectiveData[1].length() - 1);
            System.out.println("JSON Data String:\n" + data);

            JSONArray jsonArray = (JSONArray) jsonObject.get("results");
            System.out.println("Total Data set: " + jsonArray.size());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return data;
    }*/
    
    
    private String getDataFromJSON(String jsonString) {
		String data = "";
		String[] effectiveData = jsonString.split(":", 2);
		// System.out.println(effectiveData[1]);
		data = effectiveData[1].substring(0, effectiveData[1].length() - 1);
		System.out.println("JSON Data String:\n" + data);
		return data;
	}

	private String getJsonStringFromURL(String url) {
		String data = "";
		String allData = getJsonDataFromURL(url);
		String[] effectiveData = allData.split(":", 2);
		// System.out.println(effectiveData[1]);
		data = effectiveData[1].substring(0, effectiveData[1].length() - 1);
		System.out.println("JSON Data String:\n" + data);
		return data;
	}

	private String getJsonDataFromURL(String sURL) {
		String data = "";
		try {
			URL url = new URL(sURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				// System.out.println(output);
				data += output.trim();
			}
			System.out.println(data);

			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
    
    /*public String generatePdfReport(String jsonFilePathWithName, String outputFilePath) {
		int count = 1;
		JsonDataReader dataReader = new JsonDataReader();
		try {
			try {
				String rawJsonData = dataReader.getJsonString(jsonFilePathWithName);
				if(!rawJsonData.isEmpty()) {
					JasperReport report = (JasperReport) JRLoader
							.loadObject(TEMPLATE_FILE_PATH);
					ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(rawJsonData.getBytes());
					JsonDataSource ds = new JsonDataSource(jsonDataStream);
					Map parameters = new HashMap();
					parameters.put("columnCount", count++);
					JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, ds);
					// Export and save pdf to file
					String outputFilePathWithName = outputFilePath+"\\Report.pdf";
					JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePathWithName);
					System.out.println("Report is created.");
					return outputFilePathWithName;
				}else {
					System.out.println("Could not found data. Report is not created.");
					return "";
				}
				
			} catch (JRException ex) {
				ex.printStackTrace();
				throw new RuntimeException(ex);
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new RuntimeException(ex);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}*/


}
