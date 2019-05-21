package Instruct;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Instruct extends JPanel{
	public static final int totalInstructs = 11;
	public static final int alloc = 0;
	public static final int free = 1;
	
	private int current = -1;
	private String []instructStr = {"作业1申请130K", "作业2申请60K", "作业3申请100K", "作业2释放60K",
									"作业4申请200K", "作业3释放100K", "作业1释放130K", "作业5申请140K", 
									"作业6申请60K", "作业7申请50K", "作业6释放60K"};
	private int []jobId = {1, 2, 3, 2, 4, 3, 1, 5, 6, 7, 6};
	private int []op = {alloc, alloc, alloc, free, alloc, free, free, alloc, alloc, alloc, free};
	private int []size = {130, 60, 100, 60, 200, 100, 130, 140, 60, 50, 60};
	private JLabel []instructs = new JLabel[totalInstructs];
	
    public Instruct() {
    	this.setLayout(new GridLayout(totalInstructs, 1));
		Font font = new Font("Microsoft YaHei UI", Font.PLAIN, 18);
    	for (int i = 0; i < totalInstructs; ++i) {
    		instructs[i] = new JLabel(instructStr[i]);
    		instructs[i].setFont(font);
    		instructs[i].setHorizontalAlignment(JLabel.CENTER);
    		// 设置后才能修改背景
    		instructs[i].setOpaque(true);
    		instructs[i].setBackground(Color.WHITE);
    		this.add(instructs[i]);
    	}
    	this.setBackground(Color.WHITE);
    }
    
    public int getCurrent() {
    	return current;
    }
    
    public void setCurrent(int c) {
    	current = c;
    }
    
    public int[] getCurrentInstruct() {
    	int []temp = {jobId[current], op[current], size[current]};
    	return temp;
    }
    
    public void nextInstruct() {
    	++current;
    }
    
    public void preInstruct() {
    	--current;
    }
    
    public void runNextStep() {
    	if (current != 0) {
        	instructs[current-1].setBackground(Color.WHITE);
    	}
    	if (current < totalInstructs) {
        	instructs[current].setBackground(Color.ORANGE);
    	}
    }
}