package com.btpb.servive;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3140212586831417310L;

	public Main() {

		super("MLSC 1.0.1");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		} catch (InstantiationException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {

			e.printStackTrace();
		}
		setBackground(Color.decode("#C9D6E3"));

		setSize(500, 250);

		JTabbedPane jpane = new JTabbedPane();
		jpane.add("Statistics Computer", statisticsComputer());
		jpane.add("Handle Checkpoints", checkPointAdder());
		setLayout(new FlowLayout());
		add(jpane);
		((JPanel) getContentPane()).setOpaque(false);

		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public JPanel statisticsComputer() {
		JPanel content = new JPanel();
		content.setLayout(new GridBagLayout());
		content.setBackground(Color.decode("#C9D6E3"));
		content.setOpaque(false);

		JLabel label2 = new JLabel(" Path of MUTE Log Directory:");
		JLabel space = new JLabel(" ");

		JTextField logPath = new JTextField(15);
		LogFileHandler.jb = new JProgressBar(0, 100);
		LogFileHandler.jb.setBounds(40, 40, 160, 30);
		LogFileHandler.jb.setValue(0);
		LogFileHandler.jb.setStringPainted(true);
		LogFileHandler.jb.setVisible(false);

		JButton open2 = new JButton("...");
		open2.setForeground(Color.decode("#46483A"));
		open2.setBackground(Color.decode("#a8b6bf"));
		open2.setMinimumSize(getMinimumSize());
		open2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser c = new JFileChooser();
				c.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				c.setCurrentDirectory(new File(logPath.getText()));
				int rVal = c.showOpenDialog(Main.this);
				if (rVal == JFileChooser.APPROVE_OPTION) {
					logPath.setText(c.getCurrentDirectory().toString() + "\\" + c.getSelectedFile().getName());
				}
				if (rVal == JFileChooser.CANCEL_OPTION) {
					logPath.setText("");

				}

			}
		});

		label2.setForeground(Color.BLACK);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		content.add(label2, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 40;
		content.add(logPath, gbc);
		gbc.gridwidth = 1;
		gbc.gridx = 40;
		gbc.gridy = 1;
		content.add(open2, gbc);
		gbc.gridx = 40;
		gbc.gridy = 2;
		content.add(space, gbc);
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 41;
		JButton run = new JButton("Compute Statistics");
		run.setBackground(Color.decode("#a8b6bf"));
		run.setForeground(Color.decode("#46483A"));
		run.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (logPath.getText().equals("")) {
					JOptionPane.showMessageDialog(new JFrame(), "Please select Log file/directory", "Dialog",
							JOptionPane.ERROR_MESSAGE);
				} else {

					Runnable runner = new Runnable() {
						public synchronized void run() {

								try {
									run.setText("Computing..");
									LogFileHandler.jb.setVisible(true);
									LogFileHandler.jb.setValue(0);

									String logFilePath = logPath.getText();
									String logOutputPath = (!logFilePath.contains(".log"))
											? logFilePath + "\\" + "Statistics_Report.htm"
											: logFilePath.split("\\.log")[0] + "_" + "Statistics_Report.htm";

									LogFileHandler.writeFile(
											LogFileHandler.generateStatistics(
													LogFileHandler.checkScenarios(
															LogFileHandler.segregateScenarios(new File(logFilePath)))),
											logOutputPath);

									LogFileHandler.jb.setValue(99);
									run.setText("Opening Report..");
									try {
										Thread.sleep(2000);
									} catch (Exception e) {
									}
									LogFileHandler.jb.setValue(100);
									Runtime r = Runtime.getRuntime();
									r.exec("cmd.exe /c " + logOutputPath);

									run.setText("Compute Statistics");
									LogFileHandler.jb.setVisible(false);

								} catch (IOException e) {

									e.printStackTrace();
								}
							}

					};
					Thread t = new Thread(runner, "Code Executer");
					t.start();
				}

			}

		});
		content.add(run, gbc);
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 41;
		content.add(LogFileHandler.jb, gbc);

		return content;
	}

	public JPanel checkPointAdder() {
		JPanel content = new JPanel();
		content.setLayout(new GridBagLayout());
		content.setBackground(Color.decode("#C9D6E3"));
		content.setOpaque(false);

		JLabel label1 = new JLabel(" MUTE Workspace Path:");
		JLabel space = new JLabel("    ");
		JLabel space2 = new JLabel("                  ");
		JLabel label2 = new JLabel(" Session File Name:");
		JTextField workspacePath = new JTextField(15);
		JTextField ssnName = new JTextField(15);

		JButton open1 = new JButton("...");
		open1.setBackground(Color.decode("#a8b6bf"));
		open1.setForeground(Color.decode("#46483A"));
		open1.setMinimumSize(getMinimumSize());
		open1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser c = new JFileChooser();
				c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				c.setCurrentDirectory(new File(workspacePath.getText()));
				int rVal = c.showOpenDialog(Main.this);
				if (rVal == JFileChooser.APPROVE_OPTION) {
					workspacePath.setText(c.getCurrentDirectory().toString() + "\\" + c.getSelectedFile().getName());
				}
				if (rVal == JFileChooser.CANCEL_OPTION) {
					workspacePath.setText("");

				}

			}
		});

		JButton open2 = new JButton("...");
		open2.setForeground(Color.decode("#46483A"));
		open2.setBackground(Color.decode("#a8b6bf"));
		open2.setMinimumSize(getMinimumSize());
		open2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser c = new JFileChooser();
				c.setCurrentDirectory(new File(
						workspacePath.getText().split("\\\\MUTE_workspace")[0] + "\\MUTE_workspace\\Testcases\\ssn"));
				int rVal = c.showOpenDialog(Main.this);
				if (rVal == JFileChooser.APPROVE_OPTION) {
					ssnName.setText(c.getSelectedFile().getName());
				}
				if (rVal == JFileChooser.CANCEL_OPTION) {
					ssnName.setText("");

				}

			}
		});

		label1.setForeground(Color.BLACK);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		content.add(label1, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 40;

		content.add(workspacePath, gbc);
		gbc.gridwidth = 1;
		gbc.gridx = 41;
		gbc.gridy = 1;
		content.add(open1, gbc);
		gbc.gridx = 42;
		gbc.gridy = 1;
		content.add(space2, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 2;
		content.add(label2, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 40;
		content.add(ssnName, gbc);
		gbc.gridwidth = 1;
		gbc.gridx = 41;
		gbc.gridy = 3;
		content.add(open2, gbc);
		gbc.gridx = 0;
		gbc.gridy = 4;
		content.add(space, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 5;

		gbc.gridwidth = 10;
		JButton run = new JButton("Add Checkpoints");
		run.setBackground(Color.decode("#a8b6bf"));
		run.setForeground(Color.decode("#46483A"));
		run.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				Runnable runner = new Runnable() {
					public synchronized void run() {

						run.setText("Adding..");
						if (workspacePath.getText().equals("") && ssnName.getText().equals("")) {
							JOptionPane.showMessageDialog(new JFrame(),
									"Please select MUTE workspace and Session file.", "Dialog",
									JOptionPane.ERROR_MESSAGE);
						} else if (workspacePath.getText().equals("")) {
							JOptionPane.showMessageDialog(new JFrame(), "Please select MUTE workspace.", "Dialog",
									JOptionPane.ERROR_MESSAGE);
						} else if (ssnName.getText().equals("")) {
							JOptionPane.showMessageDialog(new JFrame(), "Please select Session file.", "Dialog",
									JOptionPane.ERROR_MESSAGE);
						} else {
							MUTEWorkspaceHandler mwh = new MUTEWorkspaceHandler(
									workspacePath.getText().split("\\\\MUTE_workspace")[0] + "\\MUTE_workspace");
							mwh.addCheckpoints(ssnName.getText());
						}

						run.setText("Add Checkpoints");

					}
				};
				Thread t = new Thread(runner, "Code Executer");
				t.start();

			}

		});
		content.add(run, gbc);
		gbc.gridx = 21;
		gbc.gridy = 5;
		gbc.gridwidth = 10;
		JButton run2 = new JButton("Clear Checkpoints");
		run2.setBackground(Color.decode("#a8b6bf"));
		run2.setForeground(Color.decode("#46483A"));
		run2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				Runnable runner = new Runnable() {
					public synchronized void run() {

						run2.setText("Clearing..");

						if (workspacePath.getText().equals("")) {
							JOptionPane.showMessageDialog(new JFrame(), "Please select MUTE workspace.", "Dialog",
									JOptionPane.ERROR_MESSAGE);
						} else {
							MUTEWorkspaceHandler mwh = new MUTEWorkspaceHandler(
									workspacePath.getText().split("\\\\MUTE_workspace")[0] + "\\MUTE_workspace");
							mwh.clearCheckpoints();
						}
						run2.setText("Clear Checkpoints");

					}
				};
				Thread t = new Thread(runner, "Code Executer 2");
				t.start();
			}
		});
		content.add(run2, gbc);
		return content;
	}

	public static void main(String[] args) {
		new Main();
	}

}
