package org.cytoscape.data.reader.graphml;

public enum GraphMLToken {

	ID("id"), GRAPH("graph"), EDGEDEFAULT("edgedefault"), DIRECTED("directed"),
	UNDIRECTED("undirected"), KEY("key"), FOR("for"), ALL("all"), ATTRNAME("attr.name"),
	ATTRTYPE("attr.type"), DEFAULT("default"), NODE("node"), EDGE("edge"),
	SOURCE("source"), TARGET("target"), DATA("data"), TYPE("type"),
	INT("int"), INTEGER("integer"), LONG("long"), FLOAT("float"), DOUBLE("double"),
	REAL("real"), BOOLEAN("boolean"), STRING("string"), DATE("date");
	
	private String tag;
	
	private GraphMLToken(final String tag) {
		this.tag = tag;
	}
	
	public String getTag() {
		return this.tag;
	}
	
	public static GraphMLToken getType(final String tag) {
		for (GraphMLToken token : GraphMLToken.values()) {
			if(token.getTag().equals(tag)) {
				return token;
			}
		}
		
		return null;
	}

}
