
/**
 * ComputerArchitecture Project.
 *
 * @author (6461-Group3)
 * @version 1.2
 * 9/21/2018
 */
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import javax.swing.*;

public class UI {
	private static MemorySystem ms = new MemorySystem();

	// the main frame
	private static JFrame jf = new JFrame("Simulator");

	// status of power
	private static boolean pwr_status = false;

	// status of s0-s15
	private static boolean[] switch_status = new boolean[16];

	// panel component with flow layout
	private static JPanel panel_south = new JPanel(new FlowLayout());
	private static JPanel panel_north = new JPanel(new FlowLayout());
	private static JPanel panel_central = new JPanel(null);

	// vertical box on the west side
	private static Box vbox_west = Box.createVerticalBox();
	private static Box vbox_east = Box.createVerticalBox();

	// switches from s0 to s14, operation buttons, register input buttons, IPL
	// button, Power button
	public static JButton[] array_switch_button = new JButton[16];
	private static JButton[] array_input_to_register_button = new JButton[5];

	private static JButton[] array_operation_button = new JButton[4];
	private static JButton btnIPL = new JButton("IPL");
	private static JButton btnPwr = new JButton("Power");
	private static JButton btn_reset_switches = new JButton("reset switches");
//	private static JButton btn_execute_from_pc = new JButton("Execute from pc");

	// registers labels
	private static JLabel[] array_regs_lable = new JLabel[15];
	private static JLabel current_mem_addr_label = new JLabel("Current memory address");
	private static JLabel console_information = new JLabel("Console information");
	private static JLabel hint_for_addr_input = new JLabel(
			"<html>" + "Input the 1st index of the" + "<br>" + "address you want to edit here" + "<html>");
	private static JLabel addr_mar_poiunted_label = new JLabel("Contents which MAR points to");

	// text fields for displaying registers
	private static JTextField[] array_regs_text = new JTextField[15];
	private static JTextField current_mem_addr_text = new JTextField(16);
	private static JTextField addr_to_input_text = new JTextField(16);
	private static JTextField addr_mar_pointed = new JTextField(16);

	// text area and scroll panel for displaying console information
	private static JTextArea screen_for_console = new JTextArea();
	private static JScrollPane scrollPane = new JScrollPane(screen_for_console,
			ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

	// container of main frame
	private static Container contentPane = jf.getContentPane();

	// use for fetching console printout
	private static ByteArrayOutputStream temp_stream = new ByteArrayOutputStream(100000);
	private static PrintStream cacheStream = new PrintStream(temp_stream);

	// use for add register input buttons and operation buttons
	private static int y1 = 18;

//	private static int step_status = 0; // step 0-4 for single step mode

	private static void paint() {
		// initiate main frame
		jf.setSize(1150, 750);
		jf.setResizable(true);
		jf.setLocationRelativeTo(null);
		jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		// main frame uses the border layout
		jf.setLayout(new BorderLayout());

		// initiate switch buttons
		for (int i = 0; i < array_switch_button.length; i++) {
			array_switch_button[i] = new JButton("<html>" + "S" + Integer.toString(i) + "<br>" + "[0]" + "</html>");
			array_switch_button[i].setFocusable(false);
			switch_status[i] = false;
		}

		// add switch buttons to panel
		for (int i = 0; i < array_switch_button.length; i++) {
			panel_south.add(array_switch_button[i]);
		}

		// initiate operation buttons
		array_operation_button[0] = new JButton("Run");
		array_operation_button[1] = new JButton("Halt");
		array_operation_button[2] = new JButton("Single Step");
		array_operation_button[3] = new JButton("Input to this address");

		// add operation buttons to the east panel
		vbox_east.add(Box.createVerticalStrut(50));
		for (int i = 0; i < array_operation_button.length; i++) {
			vbox_east.add(array_operation_button[i]);
			if (i == array_operation_button.length - 1) {
				vbox_east.add(Box.createVerticalStrut(5));
			} else {
				vbox_east.add(Box.createVerticalStrut(30));
			}
		}
		vbox_east.add(addr_to_input_text);
		addr_to_input_text.setMaximumSize(new Dimension(400, 23));
		vbox_east.add(hint_for_addr_input);
		vbox_east.add(Box.createVerticalStrut(30));
		vbox_east.add(addr_mar_pointed);
		addr_mar_pointed.setMaximumSize(new Dimension(400, 23));
		vbox_east.add(Box.createVerticalStrut(5));
		vbox_east.add(addr_mar_poiunted_label);
		vbox_east.add(Box.createVerticalStrut(30));
//		vbox_east.add(btn_execute_from_pc);
//		vbox_east.add(Box.createVerticalStrut(30));
		vbox_east.add(btn_reset_switches);

		// initiate register labels
		for (int i = 0; i < 8; i++) {
			if (i < 4) {
				array_regs_lable[i] = new JLabel("R" + Integer.toString(i));
			} else {
				array_regs_lable[i] = new JLabel("X" + Integer.toString(i - 4));
			}
		}
		array_regs_lable[8] = new JLabel("PC");
		array_regs_lable[9] = new JLabel("MAR");
		array_regs_lable[10] = new JLabel("MBR");
		array_regs_lable[11] = new JLabel("MFR");
		array_regs_lable[12] = new JLabel("CC");
		array_regs_lable[13] = new JLabel("IR");
		array_regs_lable[14] = new JLabel("rTemp");

		// add register labels to the west vertical box, initiate register text fields
		// and add to west vertical box
		for (int i = 0; i < array_regs_lable.length; i++) {
			vbox_west.add(array_regs_lable[i]);
			array_regs_text[i] = new JTextField(16);
			vbox_west.add(array_regs_text[i]);
		}

		// add IPL and Power button to panels
		panel_north.add(btnPwr);
		panel_north.add(btnIPL);

		// add other elements to central area
		panel_central.add(current_mem_addr_text);
		current_mem_addr_text.setBounds(290, 70, 200, 20);

		panel_central.add(current_mem_addr_label);
		current_mem_addr_label.setBounds(315, 50, 200, 20);

		panel_central.add(console_information);
		console_information.setBounds(330, 110, 200, 20);

		for (int i = 0; i < array_input_to_register_button.length; i++) {
			array_input_to_register_button[i] = new JButton("Input to here");
			panel_central.add(array_input_to_register_button[i]);
			array_input_to_register_button[i].setBounds(7, y1, 110, 20);
			if (i == array_input_to_register_button.length - 2) {
				y1 += 201;
			} else {
				y1 += 40;
			}
		}

		// set icon of power switch
		btnPwr.setIcon(new ImageIcon("off.png"));

		// initiate the text area of screen
		panel_central.add(scrollPane);
		scrollPane.setBounds(150, 130, 500, 450);
		screen_for_console.setLineWrap(true);

		// add panel and boxes to main frame
		contentPane.add(vbox_east, BorderLayout.EAST);
		contentPane.add(panel_central, BorderLayout.CENTER);
		contentPane.add(panel_north, BorderLayout.NORTH);
		contentPane.add(panel_south, BorderLayout.SOUTH);
		contentPane.add(vbox_west, BorderLayout.WEST);

		// set main frame visible
		jf.setVisible(true);

		// redirect output stream to display in the console information area
		System.setOut(new PrintStream(cacheStream));
	}

	// initiate the events binded with buttons
	private static void init_events(ComputerArchitecture ca) {
		// action binded to power switch
		btnPwr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (!pwr_status) {
						btnPwr.setIcon(new ImageIcon("on.png"));
						pwr_status = true;
					} else {
						btnPwr.setIcon(new ImageIcon("off.png"));
						pwr_status = false;
						clear_displayers();
						reset_switches();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		// action binded to register input buttons
		for (int i = 0; i < array_input_to_register_button.length; i++) {
			int i_temp = i;
			array_input_to_register_button[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (pwr_status) {
						if (i_temp != array_input_to_register_button.length - 1) {
							ca.setter_r(i_temp);
							display_register(ca);
						} else {
							ca.setter_pc();
							display_register(ca);
						}
					}
				}
			});
		}

		// action binded to memory input button
		array_operation_button[3].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (pwr_status) {
					ca.setter_mem(Integer.parseInt(addr_to_input_text.getText()), ms);
					System.out.println("mem[" + addr_to_input_text.getText() + "] = "
							+ Arrays.toString(ca.getter_mem(Integer.parseInt(addr_to_input_text.getText()), ms)));
					screen_update();
				}
			}
		});

		// action binded to s0-s15
		for (int i = 0; i < array_switch_button.length; i++) {
			int i_temp = i;
			array_switch_button[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JButton btn = (JButton) e.getSource();
					if (!switch_status[i_temp]) {
						btn.setText("<html>" + "S" + Integer.toString(i_temp) + "<br>" + "[1]" + "</html>");
						switch_status[i_temp] = true;
					} else {
						btn.setText("<html>" + "S" + Integer.toString(i_temp) + "<br>" + "[0]" + "</html>");
						switch_status[i_temp] = false;
					}
				}
			});
		}

		// action binded to switches reset button
		btn_reset_switches.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reset_switches();
			}
		});

		// action binded to run button
		array_operation_button[0].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (pwr_status) {
					try {
						ca.run(ms);
						screen_update();
						display_register(ca);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		// action binded to IPL button
		btnIPL.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (pwr_status) {
						cacheStream.close();
						temp_stream = new ByteArrayOutputStream(100000);
						cacheStream = new PrintStream(temp_stream);
						System.setOut(new PrintStream(cacheStream));
						ca.init(ms);
						display_register(ca);
						screen_update();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		// action binded to single-step button
		array_operation_button[2].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (pwr_status) {
					try {
						ca.run_single_step(ms);
						display_register(ca);
						screen_update();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
			
//Please reserve the codes below for more precisely single-step function :)
			
//			public void actionPerformed(ActionEvent e) {
//				if (pwr_status) {
//					try {
//						ca.stepByStep = 1;
//						int addr = 0;
//
//						switch (step_status) {
//						case 0:
//							ca.fetchFromPcToMar();
//							break;
//						case 1:
//							addr = ca.calMemAddr();
//							break;
//						case 2:
//							ca.fetchFromMemToMbr(addr, ms);
//							break;
//						case 3:
//							ca.moveMbrToIr();
//							break;
//						}
//						step_status++;
//					} catch (IOException e1) {
//						e1.printStackTrace();
//					}
//					try {
//						if (step_status == 4) {
//							ca.decode(ms);
//						}
//					} catch (Exception e2) {
//						e2.printStackTrace();
//					}
//					try {
//						if (step_status == 4) {
//							ca.pcIncrement();
//							step_status = 0;
//						}
//					} catch (IOException e1) {
//						e1.printStackTrace();
//					}
//					display_register(ca);
//				}
//			}
		});

		// action binded to halt button
		array_operation_button[1].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (pwr_status) {
					try {
						ca.init(ms);
						display_register(ca);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					System.out.println("**Program has been halted.**");
					screen_update();
				}
			}
		});
	}

	// method for reset s01-s15
	public static void reset_switches() {
		for (int i = 0; i < array_switch_button.length; i++) {
			array_switch_button[i].setText(("<html>" + "S" + Integer.toString(i) + "<br>" + "[0]" + "</html>"));
			switch_status[i] = false;
		}
	}

	// method for displaying addresses in registers
	public static void display_register(ComputerArchitecture ca) {
		String r_temp[] = new String[4];
		String x_temp[] = new String[4];
		String pc_temp, mar_temp, mbr_temp, mfr_temp, cc_temp, ir_temp, rTemp_temp;
		int index_of_mar = 0;

		for (int i = 0; i < 4; i++) {
			r_temp[i] = Arrays.toString(ca.getter_r(i));
			x_temp[i] = Arrays.toString(ca.getter_x(i));
			array_regs_text[i].setText(r_temp[i].substring(1, r_temp[i].length() - 1).replace(',', '\0'));
			array_regs_text[i + 4].setText(x_temp[i].substring(1, x_temp[i].length() - 1).replace(',', '\0'));
		}

		pc_temp = Arrays.toString(ca.getter_pc());
		array_regs_text[8].setText(pc_temp.substring(1, pc_temp.length() - 1).replace(',', '\0'));

		mar_temp = Arrays.toString(ca.getter_mar());
		array_regs_text[9].setText(mar_temp.substring(1, mar_temp.length() - 1).replace(',', '\0'));

		mbr_temp = Arrays.toString(ca.getter_mbr());
		array_regs_text[10].setText(mbr_temp.substring(1, mbr_temp.length() - 1).replace(',', '\0'));

		mfr_temp = Arrays.toString(ca.getter_mfr());
		array_regs_text[11].setText(mfr_temp.substring(1, mfr_temp.length() - 1).replace(',', '\0'));

		cc_temp = Arrays.toString(ca.getter_cc());
		array_regs_text[12].setText(cc_temp.substring(1, cc_temp.length() - 1).replace(',', '\0'));

		ir_temp = Arrays.toString(ca.getter_ir());
		array_regs_text[13].setText(ir_temp.substring(1, ir_temp.length() - 1).replace(',', '\0'));

		rTemp_temp = Arrays.toString(ca.getter_rTemp());
		array_regs_text[14].setText(rTemp_temp.substring(1, rTemp_temp.length() - 1).replace(',', '\0'));

		for (int i = 0; i < ca.getter_mar().length; i++) {
			index_of_mar += ca.getter_mar()[i] * (int) Math.pow(2, 15 - i);
		}
		addr_mar_pointed.setText(
				"mem[" + index_of_mar + "]=" + Arrays.toString(ca.getter_mem(index_of_mar, ms)).replaceAll(", ", ""));
	}

	// method for clearing register displayer
	public static void clear_displayers() {
		for (int i = 0; i < array_regs_text.length; i++) {
			array_regs_text[i].setText("");
		}
		screen_for_console.setText("");
		addr_to_input_text.setText("");
		current_mem_addr_text.setText("");
		addr_mar_pointed.setText("");
	}

	// method for updating console information area
	public static void screen_update() {
		screen_for_console.setText(temp_stream.toString());
		screen_for_console.setCaretPosition(screen_for_console.getText().length());
	}

	// all start here!
	public static void main(String[] args) throws Exception {
		paint();
		ComputerArchitecture ca = new ComputerArchitecture();
		init_events(ca);
	}
}
