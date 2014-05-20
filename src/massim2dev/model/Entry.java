package massim2dev.model;

public class Entry {
	public String value;
	public String alternative;
	public boolean isSuperClass;

	public Entry(String val, boolean is) {
		value = val;
		isSuperClass = is;
	}
	
	public Entry(String val, String alt, boolean is) {
		value = val;
		alternative = alt;
		isSuperClass = is;
	}

	@Override
	public boolean equals(Object obj) {
		return this.value.equals(obj);
	}
}