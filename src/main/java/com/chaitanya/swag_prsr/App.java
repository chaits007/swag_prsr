package com.chaitanya.swag_prsr;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.NameNotFoundException;

import io.swagger.models.HttpMethod;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.RefModel;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.HeaderParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.parser.SwaggerParser;


public class App {
	static ParsedInfo parsedInfo;
	static Swagger swagger;

	public static void main(String[] args) throws Exception {

		String location = "./resources/nasu_spec.json";
		swagger = new SwaggerParser().read(location);

		//GetPaths and Path Parameters
		Map<String, Path> paths = swagger.getPaths();
		for (Entry<String, Path> p : paths.entrySet()) {
			parsedInfo = new ParsedInfo();

			System.out.println("===============================================================================================================");
			System.out.println("PATH:" + p.getKey());
			parsedInfo.setPath(p.getKey());
			Path path = p.getValue();
			Map<HttpMethod, Operation> operations = path.getOperationMap();
			for (Entry<HttpMethod, Operation> o : operations.entrySet()) {
				parsedInfo.setMethod(o.getKey().toString());
				parsedInfo.setSummary(o.getValue().getSummary());
				parsedInfo.setOperationId(o.getValue().getOperationId());
				
				// Process Request Parameters
				for (Parameter parm : o.getValue().getParameters()) {
					if (parm.getClass() == HeaderParameter.class) {
						processHeaderParms((HeaderParameter) parm);
					} else if (parm.getClass() == PathParameter.class) {
						processPathParms((PathParameter) parm);
					} else if (parm.getClass() == QueryParameter.class) {
						processQueryParms((QueryParameter) parm);
					} else if (parm.getClass() == BodyParameter.class) {
						processBodyParms((BodyParameter) parm);
					} else {
						throw new NameNotFoundException("Unhandled Parameter Passed" + parm.getClass()); 
					}
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
									parsedInfo.setParm("Response", p2.getKey(), p2.getValue().getType(), p2.getValue().getRequired(), "Array", "XXX");
								}
							} else {
								throw new NullPointerException("Not Ref Property - " + p1.getKey()	+ " - "	+ p1.getValue().getType());
							}
						} else if (p1.getValue().getType() == "ref") {
							RefProperty refP1 = (RefProperty) p1.getValue();
							System.out.println(p1.getKey()	+ " - "	+ p1.getValue().getType() + " - Has Reference - " + refP1.getOriginalRef());
							Model propDef = swagger.getDefinitions().get(refP1.getSimpleRef());
							Map<String, Property> propDefProp = propDef.getProperties();
							for (Entry<String, Property> p2 : propDefProp.entrySet()) {
								System.out.println(p2.getKey() + " - " + p2.getValue().getType());
								parsedInfo.setParm("Response", p2.getKey(), p2.getValue().getType(), p2.getValue().getRequired(), p1.getKey(), "Normal");
							}
						} else {
							System.out.println(p1.getKey()	+ " - "	+ p1.getValue().getType());
							parsedInfo.setParm("Response", p1.getKey(), p1.getValue().getType(), p1.getValue().getRequired());
						}	
					}
				}
			}
			//*********************************
			//Print the details of the API here
			//*********************************
			parsedInfo.writeParsedInfoToExcel();
		}
	}
	
	public static void processRequestParms(Parameter parm) {
		
	}

	public static void processHeaderParms(HeaderParameter parm) {	
		System.out.println(parm.getIn() + " - " + parm.getName());
		parsedInfo.setParm(parm.getIn(), parm.getName(), parm.getType(), parm.getRequired());
	}

	public static void processPathParms(PathParameter parm) {
		System.out.println(parm.getIn() + " - " + parm.getName());
		parsedInfo.setParm(parm.getIn(), parm.getName(), parm.getType(), parm.getRequired());
	}

	public static void processQueryParms(QueryParameter parm) {
		System.out.println(parm.getIn() + " - " + parm.getName());
		parsedInfo.setParm(parm.getIn(), parm.getName(), parm.getType(), parm.getRequired());
	}

	public static void processBodyParms(BodyParameter parm) {
		BodyParameter bodyParm = (BodyParameter) parm;
		RefModel bodyModel = (RefModel) bodyParm.getSchema();
		System.out.println("body - Has Reference - " + bodyModel.get$ref()); 
		ModelImpl definitions = (ModelImpl) swagger.getDefinitions().get(bodyModel.getSimpleRef());
		List<String> reqd = definitions.getRequired();
		Map<String, Property> props = definitions.getProperties();
		
		/* ************** Need to handle array in the body ********************** */
		for (Entry<String, Property> p1 : props.entrySet()) {
			boolean valReqd = reqd.contains(p1.getKey());
			System.out.println(p1.getKey() + " - " + p1.getValue().getType());
			parsedInfo.setParm(parm.getIn(), p1.getKey(), p1.getValue().getType(), valReqd);
		}
	}
}