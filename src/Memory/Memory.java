package Memory;

import java.awt.Canvas;
import java.util.LinkedList;

import Instruct.Instruct;

class MemoryView extends Canvas {
	
}

public class Memory {
	public static final int FirstFit = 0;
	public static final int BestFit = 1;
	
	public MemoryView view = new MemoryView();
	
	private int size;
    private LinkedList<Zone> zones;
    private int fitWay;

    class Zone {
        private int size;
        private int head;
        private int jobId;
        private boolean isFree;

        public Zone(int head, int size, int jobId) {
            this.head = head;
            this.size = size;
            this.jobId = jobId;
            this.isFree = true;
        }
    }

    public Memory() {
    	// 内存大小为640K
        this.size = 640;
        this.zones = new LinkedList<>();
        zones.add(new Zone(0, size, 0));
        this.fitWay = FirstFit;
    }
    
    public boolean runInstruct(int jobId, int op, int size) {
    	switch (op) {
		case Instruct.alloc:
			return allocation(size, jobId);
		case Instruct.free:
			collection(jobId);
		}
		return true;
    }
    
    public void clear() {
    	this.zones.clear();
        this.zones.add(new Zone(0, size, 0));
    }
    
    public void setFitWay(int fit) {
    	this.fitWay = fit;
    }

	public boolean allocation(int size, int jobId) {
    	// 选择不同分配方式
        switch (this.fitWay) {
        case FirstFit:
            return this.fristFit(size, jobId);
        case BestFit:
            return this.bestFit(size, jobId);
        default:
        	return false;
        }
    }

    private boolean bestFit(int size, int jobId) {
	    int flag  = -1;
	    int min = this.size;
	    Zone bestZone = null;
	    int location = 0;
	    // 遍历分区链表
	    for (Zone zone : this.zones) {
	        if (zone.isFree && (zone.size >= size)) {
	            if (min > zone.size) {
	                min = zone.size;
	                bestZone = zone;
	                flag = location;
	            }
	        }
	        ++location;
	    }
	    // 是否成功分配
	    if (flag != -1) {
	        doAllocation(size, bestZone, jobId, flag);
	        return true;
	    } else {
	        return false;
	    }
	}

    private boolean fristFit(int size, int jobId) {
    	int location = 0;
        // 遍历分区链表
        for (Zone zone : this.zones) {
            // 分配成功
            if (zone.isFree && (zone.size >= size)) {
                doAllocation(size, zone, jobId, location);
                return true;
            }
            ++location;
        }
        // 分配失败
        return false;
    }

    private void doAllocation(int size, Zone zone, int jobId, int location) {
        // 如果当前分区较大 分区
    	if (zone.size > size) {
    		Zone split = new Zone(zone.head + size, zone.size - size, 0);
        	this.zones.add(location + 1, split);
    	}
        zone.size = size;
        zone.isFree = false;
        zone.jobId = jobId;
    }

    public void collection(int jobId) {
    	int location = 0;
    	//遍历分区链表
        for (Zone zone : this.zones) {
            if (zone.jobId == jobId) {
            	//如果回收分区不是尾分区且后一个分区为空闲, 则与后一个分区合并
                if (location < zones.size() - 1 && zones.get(location + 1).isFree) {
                    Zone next = zones.get(location + 1);
                    zone.size += next.size;
                    zones.remove(next);
                }
                //如果回收分区不是首分区且前一个分区为空闲, 则与前一个分区合并
                if (location > 0 && zones.get(location - 1).isFree) {
                    Zone previous = zones.get(location - 1);
                    previous.size += zone.size;
                    zones.remove(location);
                }
                zones.get(location).isFree = true;
                return;
            }
            ++location;
        }
    }
}