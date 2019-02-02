package com.chaitanya.swag_prsr;

import java.util.Map;
import java.util.Map.Entry;

import javax.xml.validation.Schema;

import io.swagger.models.HttpMethod;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.RefModel;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.utils.PropertyModelConverter;
import io.swagger.parser.SwaggerParser;


public class App {

	private static Map<String, Parameter> resp;

	public static void main(String[] args) {
		String location = "./resources/nasu_spec.json";
		Swagger swagger = new SwaggerParser().read(location);
		
		System.out.println(swagger);
		//GetPaths and Path Parameters
		Map<String, Path> paths = swagger.getPaths();
		for (Entry<String, Path> p : paths.entrySet()) {
			Path path = p.getValue();
			Map<HttpMethod, Operation> operations = path.getOperationMap();
			for (Entry<HttpMethod, Operation> o : operations.entrySet()) {
				System.out.println("===");
				System.out.println("PATH:" + p.getKey());
				System.out.println("Http method:" + o.getKey());
				System.out.println("Summary:" + o.getValue().getSummary());
				System.out.println("Parameters number: " + o.getValue().getParameters().size());
				for (Parameter parameter : o.getValue().getParameters()) {
					System.out.println("Parm Name: " + parameter.getName()); 
					System.out.println("Input Type: " + parameter.getIn());
				}
				System.out.println("Responses:");
				for (Map.Entry<String, Response> r : o.getValue().getResponses().entrySet()) {
					System.out.println(" - " + r.getKey() + ": " + r.getValue().getDescription());
					Model resp_mdl = r.getValue().getResponseSchema();
					String ref = resp_mdl.getReference();
					System.out.println("$ref : " + ref);
//					RefProperty pmc = (RefProperty) new PropertyModelConverter().modelToProperty(resp_mdl);
					
//					//Get Properties for each key
//					Map<String, Property> props = resp_mdl.getProperties();
//					for (Entry<String, Property> resp_p : props.entrySet()) {
//						System.out.println("****Response Property****");
//						System.out.println("Name: " + resp_p.getKey());
//						System.out.println("Desc: " + resp_p.getValue().getDescription());
//						System.out.println("Type: " + resp_p.getValue().getType());
//						System.out.println("Reqd: " + resp_p.getValue().getRequired());
//						System.out.println("Reqd: " + resp_p.getValue());	
//					}
//				}
				}
			}	
		}
		
		//Get Definitions and Properties
		Map<String, Model> models = swagger.getDefinitions();
		System.out.println("\nDefinitions:");
		for (Entry<String, Model> m : models.entrySet()) {
			Model definitions = swagger.getDefinitions().get(m.getKey());
			System.out.println("Key : " + m.getKey());
			
			//Get Properties for each key
			Map<String, Property> props = definitions.getProperties();
			for (Entry<String, Property> p : props.entrySet()) {
				System.out.println("****Property****");
				System.out.println("Name: " + p.getKey());
				System.out.println("Desc: " + p.getValue().getDescription());
				System.out.println("Type: " + p.getValue().getType());
				System.out.println("Reqd: " + p.getValue().getRequired());
				System.out.println("Reqd: " + p.getValue());	
			}
			
			System.out.println("============================");
		}		
	}
}