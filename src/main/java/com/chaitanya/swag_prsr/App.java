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
import io.swagger.models.parameters.RefParameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.utils.PropertyModelConverter;
import io.swagger.parser.SwaggerParser;


public class App {

	public static void main(String[] args) {
		String location = "./resources/nasu_spec.json";
		Swagger swagger = new SwaggerParser().read(location);
		Map<String, Model> models = swagger.getDefinitions();
		
		//GetPaths and Path Parameters
		Map<String, Path> paths = swagger.getPaths();
		for (Entry<String, Path> p : paths.entrySet()) {
			System.out.println("===============================================================================================================");
			System.out.println("===============================================================================================================");
			System.out.println("PATH:" + p.getKey());
			Path path = p.getValue();
			Map<HttpMethod, Operation> operations = path.getOperationMap();
			for (Entry<HttpMethod, Operation> o : operations.entrySet()) {
				System.out.println("Http method:" + o.getKey());
				System.out.println("Summary:" + o.getValue().getSummary());
				
				System.out.println("\n****************************************** Request Parameters *********************************************");
				for (Parameter parm : o.getValue().getParameters()) {
					processRequestParms(swagger, parm);
				}
				
				System.out.println("\n****************************************** Response Parameters *********************************************");
				for (Map.Entry<String, Response> r : o.getValue().getResponses().entrySet()) {
					System.out.println(" - " + r.getKey() + ": " + r.getValue().getDescription());
					RefModel resp_mdl = (RefModel) r.getValue().getResponseSchema();
					
					Model definitions = swagger.getDefinitions().get(resp_mdl.getSimpleRef());
					//Get Properties for each key
					Map<String, Property> props = definitions.getProperties();
					for (Entry<String, Property> p1 : props.entrySet()) {
						if (p1.getValue().getClass() == ArrayProperty.class) {
							ArrayProperty temp = (ArrayProperty) p1.getValue();
							if (temp.getItems().getClass() == RefProperty.class) {
								RefProperty refP1 = (RefProperty) temp.getItems();
								System.out.println(p1.getKey()	+ " - "	+ p1.getValue().getType() + " - Has Reference - " + refP1.getOriginalRef());
								Model propDef = swagger.getDefinitions().get(refP1.getSimpleRef());
								Map<String, Property> propDefProp = propDef.getProperties();
								for (Entry<String, Property> p2 : propDefProp.entrySet()) {
									System.out.println(p2.getKey() + " - " + p2.getValue().getType());	
								}
							} else {
								throw new NullPointerException("Not Ref Property - " + p1.getKey()	+ " - "	+ p1.getValue().getType());
							}
							System.out.println("--------------------------------------------------------");
						} else {
							System.out.println(p1.getKey()	+ " - "	+ p1.getValue().getType());	
						}	
					}
				}
			}
		}
	}
	
	public static void processRequestParms(Swagger swagger, Parameter parm) {
		
		if (parm.getClass() == RefParameter.class) {
			RefParameter refParm = (RefParameter) parm;
			System.out.println("Has Reference - " + refParm.getIn() + " - " + refParm.get$ref()); 
			Model definitions = swagger.getDefinitions().get(refParm.getSimpleRef());
			Map<String, Property> props = definitions.getProperties();
			for (Entry<String, Property> p1 : props.entrySet()) {
				System.out.println(p1.getKey() + " - " + p1.getValue().getType());	
			}
		} else {
			System.out.println(parm.getIn() + " - " + parm.getName());
		}
	}
}