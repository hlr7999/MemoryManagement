package Memory;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.FlowLayout;
import java.util.LinkedList;

import javax.swing.JPanel;

import Instruct.Instruct;

class Zone {
    public int size;
    public int head;
    public int jobId;
    public boolean isFree;

    public Zone(int head, int size, int jobId) {
        this.head = head;
        this.size = size;
        this.jobId = jobId;
        this.isFree = true;
    }
}

class MemoryView extends JPanel {
	class MyCanvas extends Canvas {
		private Graphics pen;
	    private Color color;
	    private int start;
	    private int size;
	    private int jobId;

	    public MyCanvas(Color color, int start, int size, int jobId) {
	        this.size = size;
	        setSize(280, this.size);
	        this.color = color;
	        this.start = start;
	        this.jobId = jobId;
	    }

	    @Override
	    public void paint(Graphics g) {
	        super.paint(g);
	        pen = g;
	        pen.setColor(color);
	        pen.fillRect(0, 0, 280, size);
	        
	        pen.setColor(Color.black);
	        pen.drawLine(0, 0, 280, 0);

	        pen.setFont(new Font("宋体",Font.BOLD,13));
	        pen.setColor(Color.blue);
	        if (size >= 30) {
	        	// 画首尾地址
		        pen.drawString(String.valueOf(start) , 2, 15);
		        pen.drawString(String.valueOf(start + size) , 2, size - 5);
		        // 画作业
		        if (jobId != 0) {
			        pen.setFont(new Font("宋体", Font.BOLD, 17));
			        pen.setColor(Color.red);
			        pen.drawString("作业" + jobId , 100, size/2 + 10);
		        }
	        } else {
	        	// 只画尾地址
		        pen.drawString(String.valueOf(start + size) , 2, size);
	        }
	    }

	    @Override
	    public void update(Graphics g) {
	        paint(g);
	    }
	}
	
	private Memory memory;
	
	public MemoryView (Memory memory) {
		this.memory = memory;
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
	}
	
	public void paintCanvas() {
		// 清空当前画布
        this.removeAll();
        for (Zone zone : memory.getZones()) {
            if (zone.isFree) {
                // 空闲
                MyCanvas cache = new MyCanvas(Color.gray, zone.head, zone.size, zone.jobId);
                this.add(cache);
            } else {
                MyCanvas cache = new MyCanvas(Color.orange, zone.head, zone.size, zone.jobId);
                this.add(cache);
            }
        }
        this.revalidate();
	}
}

public class Memory {
	public static final int FirstFit = 0;
	public static final int BestFit = 1;
	// 内存大小为640K
	public static final int totalMemory = 640;
	
	public MemoryView view = new MemoryView(this);
	
	private int size;
    private LinkedList<Zone> zones;
    private int fitWay;

    public Memory() {
        this.size = totalMemory;
        this.zones = new LinkedList<>();
        zones.add(new Zone(0, size, 0));
        this.fitWay = FirstFit;
        view.paintCanvas();
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
    
    public LinkedList<Zone> getZones() {
    	return zones;
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
	    int min = this.size + 1;
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
        // 如果当前分区较大
    	if (zone.size > size) {
    		Zone split = new Zone(zone.head + size, zone.size - size, 0);
        	this.zones.add(location + 1, split);
    	}
        zone.size = size;
        zone.isFree = false;
        zone.jobId = jobId;
        view.paintCanvas();
    }

    public void collection(int jobId) {
    	int location = 0;
    	//遍历分区链表
        for (int i = 0; i < zones.size(); ++i) {
        	Zone zone = zones.get(i);
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
                    zones.remove(zone);
                    zone = previous;
                }
                zone.isFree = true;
                zone.jobId = 0;
                view.paintCanvas();
            }
            ++location;
        }
    }
}