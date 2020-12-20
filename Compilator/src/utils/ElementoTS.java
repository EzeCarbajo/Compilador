package utils;

public class ElementoTS{
	private String value; 				// Para valores númericos que requieren ser guardados y cadenas (CONST o PR o cadena)
	private String tokenClass; 			// ID CONST PR CAD
	private String variableType; 		// int long
	private int variableRepatitions; 	// Cantidad de repeticiones de la variable/constante
	private boolean declared; 		  	// Para control de declaración de variables
	private String identifierClass;	  	// Define si un ID es una colección (COL) o una variable (VAR)
	private int cSize; 				  	// Tamaño de la coleccion
	private int cSizeBytes; 		  	// Tamaño de la colección en bytes
	private String elemsCollection;   	// Elementos de la colección separados por ','
	private static int idCounter = 0; 	// Generador de ID único para cadenas
	private int idCadena; 			  	// Id para identificar cadenas
	private boolean isPointer; 		  	// Define si un ID variable es puntero (32 bits sin tipo)
	private boolean foreachControl;		// Para control en los distintos casos de variables para foreach

			//// Tipos de variables ////
	public static final String INT = "int";
	public static final String LONG = "long";
			//// Clases de tokens ////
	public static final String ID = "Identifier";
	public static final String CONST = "Constant";
	public static final String PR = "Reserved word";
	public static final String CAD = "Cadena";
			//// Clases de ID ////
	public static final String VAR = "Variable";
	public static final String COL = "Collection";

	public ElementoTS(String tokenClass, String value, String variableType) {
		this.value = value;
		this.tokenClass = tokenClass;
		this.variableType = variableType;
		variableRepatitions = 1;
		declared = false;
		identifierClass = "";
		cSize = 0;
		idCadena = -1;
		cSizeBytes = 0;
		isPointer = false;
		elemsCollection = "";
		foreachControl = false;
	}

	public String getElemsCollection() {return elemsCollection;}

	public Boolean isPointer() { return isPointer; }

	public void setHasPointer() { isPointer = true; }

	public void setElemsCollection(String elems) { elemsCollection = elems;}

	public String getValue(){ return value; }

	public String getVariableType(){ return variableType; }

	public String getTokenClass() { return tokenClass; }

	public int getVariableRepetitions(){ return variableRepatitions; }

	public int getCSize() { return cSize; }

	public void increaseVariableRepetitions(){ variableRepatitions = variableRepatitions + 1; }

	public void decreaseVariableRepetitions(){ variableRepatitions = variableRepatitions - 1; }

	public void setDeclared() { this.declared = true; }

	public void setCSize(int cSize) { this.cSize = cSize;}

	public boolean isDeclared(){ return declared; }

	/*
	 * En las colecciones, el cSize (tamaño de colección) debe definirse previo al tipo
	 */
	public void setVariableType(String type){
		variableType = type;
		if (type == INT)
			cSizeBytes = cSize * 2;
		else
			cSizeBytes = cSize * 4;
	}

	public void setIdentifierClass(String identifierClass) { this.identifierClass = identifierClass; }

	public String getIdentifierClass() { return identifierClass; }

	public int getCSizeBytes() { return cSizeBytes; }

	public void setId() { idCadena = idCounter; idCounter++; }

	// Para obtener id único para cadenas
	public static int getNewId() { idCounter++; return idCounter-1; }
	
	public static void resetIdGenerator() { idCounter = 0; }

	public int getId() { return idCadena; }
	
	public void foreachControlOn() { foreachControl = true; }
	
	public void foreachControlOff() { foreachControl = false; }
	
	public boolean foreachControlState() { return foreachControl; }
	
	@Override
	public boolean equals(Object value) {
        return (this.value.equals((String)value) && (tokenClass != PR));
    }
}