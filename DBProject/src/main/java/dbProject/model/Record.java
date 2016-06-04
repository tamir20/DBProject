package dbProject.model;

public class Record {

	private final int STRING_LENGTH = 16;
	private int k;
	private String v1;
	private String v2;

	public Record() {
		this.k = -1;
		this.v1 = "";
		this.v2 = "";
	}

	public Record(Record record) {
		this.k = record.k;
		this.v1 = record.v1;
		this.v2 = record.v2;
	}

	public void setRecord(int k, String v1, String v2) {
		this.k = k;
		this.v1 = v1;
		this.v2 = v2;
	}

	public void setK(int k) {
		this.k = k;
	}

	public int getK() {
		return this.k;
	}

	public void setV1(String v1) {
		this.v1 = v1.substring(0, Math.min(v1.length(), STRING_LENGTH));
		this.v1 = String.format("%1$-" + STRING_LENGTH + "s", this.v1);
	}

	public String getV1() {
		return this.v1;
	}

	public void setV2(String v2) {
		this.v2 = v2.substring(0, Math.min(v2.length(), STRING_LENGTH));
		this.v2 = String.format("%1$-" + STRING_LENGTH + "s", this.v2);
	}

	public String getV2() {
		return this.v2;
	}
}
