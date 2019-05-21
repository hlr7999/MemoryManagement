package App;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import Instruct.Instruct;
import Memory.Memory;

public class App extends JFrame{
	private JPanel appView = new JPanel();
	private Memory memory = new Memory();
	// 控制view
	JPanel controlView = new JPanel();
	JRadioButton firstFit = new JRadioButton("首次适应算法", true);
	JRadioButton bestFit = new JRadioButton("最佳适应算法");
	JButton runByOneStep = new JButton("单步执行");
	JButton runAll = new JButton("执行全部");
	// 指令view
	private Instruct instructView = new Instruct();
	
	public App() {
		appView.setLayout(new GridLayout(1, 3, 10, 10));
		appView.setBorder(BorderFactory.createLineBorder(getBackground(), 10));
		// memory view
		appView.add(memory.view);
		// 控制view
		controlView.setLayout(new GridLayout(2, 1));
		Font font = new Font("Microsoft YaHei UI", Font.PLAIN, 20);
		Box radioButtons = Box.createVerticalBox();
		firstFit.addActionListener(firstFitButtonListener);
		bestFit.addActionListener(bestFitButtonListener);
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(firstFit);
		buttonGroup.add(bestFit);
		firstFit.setFocusPainted(false);
		firstFit.setFont(font);
		firstFit.setAlignmentX(CENTER_ALIGNMENT);
		bestFit.setFocusPainted(false);
		bestFit.setFont(font);
		bestFit.setAlignmentX(CENTER_ALIGNMENT);
		radioButtons.add(Box.createVerticalGlue());
		radioButtons.add(firstFit);
		radioButtons.add(bestFit);
		radioButtons.add(Box.createVerticalGlue());
		controlView.add(radioButtons);
		JPanel runButtons = new JPanel();
		runByOneStep.setMargin(new Insets(1, 1, 1, 1));
		runByOneStep.setFocusPainted(false);
		runByOneStep.setBackground(Color.white);
		runByOneStep.setFont(font);
		runByOneStep.setPreferredSize(new Dimension(100, 100));
		runByOneStep.addActionListener(runByOneStepListener);
		runButtons.add(runByOneStep);
		runAll.setMargin(new Insets(1, 1, 1, 1));
		runAll.setFocusPainted(false);
		runAll.setBackground(Color.white);
		runAll.setFont(font);
		runAll.setPreferredSize(new Dimension(100, 100));
		runAll.addActionListener(runAllListener);
		runButtons.add(runAll);
		controlView.add(runButtons);
		appView.add(controlView);
		// 指令view
		appView.add(instructView);
		
		this.add(appView);
		setTitle("Memory Management");
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	setSize(900, 688);
    	setResizable(false);
    	setVisible(true);
	}
	
	ActionListener firstFitButtonListener = new ActionListener() {		
		@Override
		public void actionPerformed(ActionEvent e) {
			memory.setFitWay(Memory.FirstFit);
		}
	};
	
	ActionListener bestFitButtonListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			memory.setFitWay(Memory.BestFit);
		}
	};
	
	ActionListener runByOneStepListener = new ActionListener() {	
		@Override
		public void actionPerformed(ActionEvent e) {
			if (instructView.getCurrent() == Instruct.totalInstructs) {
				instructView.setCurrent(-1);
				memory.clear();
			}
			runInstrut();
		}
	};
	
	ActionListener runAllListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (instructView.getCurrent() == Instruct.totalInstructs) {
				instructView.setCurrent(-1);
				memory.clear();
			}
			runByOneStep.setEnabled(false);
			firstFit.setEnabled(false);
			bestFit.setEnabled(false);
			runAll.setEnabled(false);
			Timer timer = new Timer(true);
			TimerTask timerTask = new TimerTask() {
				@Override
				public void run() {
					if (instructView.getCurrent() < Instruct.totalInstructs)
						runInstrut();
					else {
						this.cancel();
						timer.cancel();
						runByOneStep.setEnabled(true);
						firstFit.setEnabled(true);
						bestFit.setEnabled(true);
						runAll.setEnabled(true);
					}
				}
			};
			timer.schedule(timerTask, 0, 1000);
		}
	};
	
	public void runInstrut() {
		instructView.nextInstruct();
		if (instructView.getCurrent() < Instruct.totalInstructs) {
			int []temp = instructView.getCurrentInstruct();
			if (!memory.runInstruct(temp[0], temp[1], temp[2])) {
				instructView.preInstruct();
				JOptionPane.showMessageDialog(appView, "内存分配失败");
				return;
			}
		}
		instructView.runNextStep();
	}
    
    public static void main(String[] args) {
        new App();
    }

}