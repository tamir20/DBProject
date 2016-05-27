package dbProject;

import java.util.LinkedList;
import java.util.List;

public class Disk {

	private final int DISK_LENGTH = 1000;
	private Record[] disk;
	private List<Integer> freeSpace;

	public Disk() {

		// initiate the structures
		this.disk = new Record[DISK_LENGTH];
		this.freeSpace = new LinkedList<Integer>();

		// initiate the records and the free space
		for (int i = 0; i < DISK_LENGTH; i++) {
			this.disk[i] = new Record();
			this.freeSpace.add(i);
		}
	}

	// return the RID of the allocated record
	public int allocateRecord(int k, String v1, String v2) {

		if (this.freeSpace.isEmpty()) {
			return -1;
		}

		int rid = this.freeSpace.remove(0);
		this.disk[rid].setRecord(k, v1, v2);

		return rid;
	}

	public Boolean freeRecord(int rid) {

		if (this.freeSpace.contains(rid)) {
			return true;
		}

		this.freeSpace.add(rid);

		return true;
	}

	public Record readRecord(int rid) {
		Record record = new Record(this.disk[rid]);
		return record;
	}

}
