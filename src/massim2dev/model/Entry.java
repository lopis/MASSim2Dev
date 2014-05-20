package massim2dev.model;

public class Entry {
	public String value;
	public String alternative;
	public boolean isSuperClass;

	public Entry(String s, boolean is) {
		value = s;
		isSuperClass = is;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.value.equals(obj);
	}
}