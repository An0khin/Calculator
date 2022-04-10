package com.home;

import javax.swing.*; //for GUI
import java.io.*; //for exceptions
import java.awt.*; //for buttons
import java.awt.event.*; //for buttons
import java.util.*; //for arrays
import javax.swing.filechooser.*; //for FileNameExtensionFilter

public class Calculator
{
	//array of operations' priority
	String[][] priorityAr = {{"fact", "sqrt", "abs", "log10", "sin", "cos"},{"("}, {"^"}, {"*", "/", "%"}, {"+", "-"}};
	//labels of the buttons
	String[] numbs = {"1", "2", "3", 
					  "4", "5", "6", 
					  "7", "8", "9", 
					  "C", "0", "DEL",
					  "+/-", "."};
	String[] actions = {"+", "-", "(", ")", "sqrt",
						"abs", "log10", "sin", "cos",
						"fact",
						"*", "/", "%", "^", "="};

	ArrayList<String> names = new ArrayList<String>();
	java.util.List<String> namesOnly = new ArrayList<String>();
	ArrayList<String> definitions = new ArrayList<String>();
	Loader loader;

	JTextField line; //line of the expression
	JFrame frame; //main frame
	ArrayList<Integer> counts = new ArrayList<Integer>();
	public static void main(String[] args)
	{
		Calculator calc = new Calculator();
		calc.go();
	}
	public void go()
	{
		setFrame(); //Setting GUI
		loader = new Loader();
		loader.start();
	}
	public void setFrame() //Setting GUI
	{
		frame = new JFrame("Calculator");
		//general layout of the layouts of the buttons
		JPanel center = new JPanel(new BorderLayout());

		//layouts of the buttons
		JPanel buttonsNumbs = new JPanel(new GridLayout(5,3));
		JPanel buttonsActions = new JPanel(new GridLayout(2,5));
		JPanel userButtonsActions = new JPanel(new GridLayout(10,10));
		
		line = new JTextField();
		line.setEditable(false);

		//creating buttons
		for(String s : numbs)
		{
			JButton but = new JButton(s);
			but.addActionListener(new ButtonListener());
			buttonsNumbs.add(but);
		}
		for(String s : actions)
		{
			JButton but = new JButton(s);
			but.addActionListener(new ButtonListener());
			buttonsActions.add(but);
		}
		for(String s : names)
		{
			JButton but = new JButton(s);
			but.addActionListener(new ButtonListener());
			userButtonsActions.add(but);
		}

		//adding buttons to the screen
		center.add(BorderLayout.CENTER, buttonsNumbs);
		center.add(BorderLayout.SOUTH, buttonsActions);
		center.add(BorderLayout.EAST, userButtonsActions);

		//adding layout of the buttons and line of the expression to the screen
		frame.getContentPane().add(BorderLayout.CENTER, center);
		frame.getContentPane().add(BorderLayout.NORTH, line);

		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem create = new JMenuItem("Create operations");
		create.addActionListener(new Creator());
		JMenuItem load = new JMenuItem("Load operations(.txt)");
		load.addActionListener(new Loader());

		file.add(create);
		file.add(load);
		
		bar.add(file);

		frame.setJMenuBar(bar);

		//showing frame on the screen
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500,500);
		frame.setResizable(false);
		frame.setVisible(true);
	}
	public String calculate(java.util.List<String> parts)
	{
		//System.out.println(parts);
		//Something arithmetics
		String result = "";

		//System.out.println(parts + " begin calc");

		boolean isSem = false;
		act123:for(int i = 0; i < parts.size(); i++)
		{
			for(int k = 0; k < namesOnly.size(); k++)
			{
				//System.out.println(parts.get(i).substring(0, namesOnly.get(k).length()) + "l");
				//System.out.println(namesOnly.get(k) + "l");
				if(parts.get(i).indexOf(";") != -1 || (parts.get(i).length() >= namesOnly.get(k).length() &&
					(parts.get(i).substring(0, namesOnly.get(k).length())).equals(namesOnly.get(k))))
				{
					isSem = true;
					break act123;
				}else
				{
					isSem = false;
				}
			}
		}

		//System.out.println(namesOnly);
		if(isSem){
			calculateSem(isSem, parts);
		} else
		{
			calculateNoSem(parts);	
		}

		if(parts.size() > 1) //if there is no end answer
			calculate(parts);
			
		result = parts.get(0);

		//System.out.println(result + "\n" + parts);
		return result;
	}
	public void calculateSem(boolean isSem, java.util.List<String> parts)
	{
		//System.out.println("calculateSem");
		if(namesOnly.size() > 0)
		{
			act0:for(int name = 0; name < namesOnly.size(); name++)
			{
				for(int i = 0; i < parts.size(); i++)
				{
					//System.out.println(namesOnly.get(name) + " name / " + parts.get(i).substring(0, namesOnly.get(name).length()) + " substr");
					//System.out.println((parts.get(i).substring(0, namesOnly.get(name).length())).indexOf(namesOnly.get(name)) + " index");


					//if(parts.get(i).length() >= namesOnly.get(name).length() && (parts.get(i).substring(0, 
					//	namesOnly.get(name).length())).indexOf(namesOnly.get(name)) != -1)
					if(parts.get(i).length() >= namesOnly.get(name).length() && (parts.get(i).substring(0,
						namesOnly.get(name).length())).equals(namesOnly.get(name)))
					{
						String exp = parts.get(i).substring(namesOnly.get(name).length() + 1, parts.get(i).length()-1);

						//String[] expAr = exp.split(" ; ");

						ArrayList<String> expAr = new ArrayList<String>();

						String part = "";

						int count = -1;
						int place = -1;

						for(int c = 0; c < definitions.get(name).length(); c++)
						{
							if(Character.toString(definitions.get(name).charAt(c)).equals("X"))
								count++;
						}

						int br = 0;

						for(int c = 0; c < exp.length(); c++)
						{
							String l = Character.toString(exp.charAt(c));
							if(br <= 0 && l.equals(";"))
							{
								expAr.add(part);
								part = "";
							} else
							{
								part += l;
							}
							
							if(l.equals("("))
							{
								br++;
							} else if(l.equals(")"))
							{
								br--;
							}
						}
						expAr.add(part);

						exp = definitions.get(name);

						//System.out.println(parts + " parts");
						//System.out.println(expAr + " expAr");
						//System.out.println(exp + " exp");

						for(int temp = 0; temp < expAr.size(); temp++)
							exp = exp.replaceFirst("X", expAr.get(temp));
						//System.out.println(exp);

						String part2 = calculate(divide(exp));
						parts.remove(parts.get(i));
						parts.add(i, part2);

						break act0;
					}
				}
			}
		} else {
			calculateNoSem(parts);
		}
	}
	public void calculateNoSem(java.util.List<String> parts)
	{
		//System.out.println("calculateNoSem");
		act:for(String[] s : priorityAr)
		{
			for(int i = 0; i < parts.size(); i++)
			{
				boolean func = false;
				String op = "";
				check:for(String ss : priorityAr[0])
				{
					if(parts.get(i).length() > ss.length() && ss.equals(op = parts.get(i).substring(0, ss.length())))
					{
						func = true;
						//System.out.println(op);
						break check;
					}
				}
				if(Arrays.asList(s).contains(Character.toString(parts.get(i).charAt(0))) || func) //checking for actions by priority
				{
					//System.out.println("Checking");
					if(Character.toString(parts.get(i).charAt(0)).equals("(")) //if "(" starts
					{
						String expression = parts.get(i).substring(1, parts.get(i).length()-1);
						//System.out.println(expression);
						String part1 = calculate(divide(expression));
						parts.remove(parts.get(i));
						parts.add(i, part1);	
						//	System.out.println(parts);
						break act;
					}else //if "(" doesn't start
					{
						//System.out.println("Without (");
						String part;
						if (func)
						{
							//System.out.println(parts.get(i).substring(op.length() + 2, parts.get(i).length() - 1));
							//System.out.println(parts);
							String sub = parts.get(i).substring(op.length() + 2, parts.get(i).length() - 1);
							if(sub.indexOf("(") != -1)
							{
								part = calculate(divide(sub));
								part = parts.get(i).substring(0, op.length() + 2) + part + " )";
							}else
							{
								part = calc2Numbers(op, sub, "0");
							}
							parts.remove(parts.get(i));
							parts.add(i, calculate(divide(part)));
							//func = false;
						}else
						{
							part = calc2Numbers(parts.get(i), parts.get(i-1), parts.get(i+1));
							for(int k = 0; k < 3; k++)
								parts.remove(parts.get(i-1));
							parts.add(i-1, part);
						}
						
						
						//System.out.println(parts.toString());
						break act;
					}
				}
			}
		}
	}
	public String calc2Numbers(String operation, String a, String b)
	{
		//System.out.println(a + " " + operation + " " + b + "   calc2numb");
		double a1 = Double.valueOf(a);
		double a2 = Double.valueOf(b);
		double result = 0;
		switch(operation)
		{
			case "^":
				result = Math.pow(a1, a2); 
				break;
			case "*":
				result = a1 * a2;
				break;
			case "/":
				result = a1 / a2;
				break;
			case "%":
				result = a1 % a2;
				break;
			case "+":
				result = a1 + a2;
				break;
			case "-":
				result = a1 - a2;
				break;
			case "sqrt":
				result = Math.sqrt(a1);
				break;
			case "abs":
				result = Math.abs(a1);
				break;
			case "log10":
				result = Math.log10(a1);
				break;
			case "sin":
				result = Math.sin(a1);
				break;
			case "cos":
				result = Math.cos(a1);
				break;
			case "fact":
				result = fact(a1);
				break;
		}
		return Double.toString(result);
	}
	public double fact(double a)
	{
		double result = 1;

		for (double factor = 1; factor <= a; factor++) {
            result *= factor;
        }

        return result;
	}
	public java.util.List<String> divide(String expression) //dividing expression to parts
	{
		java.util.List<String> parts = new ArrayList<String>();
		String word = "";
		String let = "";
		boolean brackets = false;
		int countBr = 0;

		for(int i = 0; i < expression.length(); i++)
		{
			if((let = Character.toString(expression.charAt(i))).equals("(")) //if "(" then next symbols before ")" are one part
			{
				brackets = true;
				countBr++;
			} else if(let.equals(")"))
				countBr--;
			if(countBr <= 0) //if ")" then brackets are over
				brackets = false;
				
			if(!brackets && !let.equals(" ")) //adding numbers to part
				word += let;
			else if(!brackets && let.equals(" ")) //adding part to parts' array
			{
				if(!word.isEmpty())
					parts.add(word);
				word = "";
			} else //adding numbers into brackets to one part
			{
				//if(!let.equals("(") && !let.equals(")"))
				word += let;
			}
			//System.out.println(let + " / " + word);
		}
		if(!word.isEmpty()) //adding last part to parts' array
			parts.add(word);
		//System.out.println(parts + " divide");

		return parts;

	}
	class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent ae)
		{
			JButton b = (JButton) ae.getSource(); //receiving object that called this method
			java.util.List tempList = Arrays.asList(actions);
			String lineText = line.getText();
			String text;
			int count;
			if(names.contains(text = b.getText()))
			{
				switch(text)
				{
					case ">>>":
						if(counts.get(counts.size() - 1) > 1)
						{
							counts.set(counts.size() - 1, counts.get(counts.size() - 1) - 1);
							line.setText(lineText + " ; ");
						} else
						{
							counts.remove(counts.size() - 1);
							if(counts.size() > 0 && counts.get(counts.size() - 1) > 1)
							{
								line.setText(lineText + " ) ; ");
								counts.set(counts.size() - 1, counts.get(counts.size() - 1) - 1);
							} else if(counts.size() > 0)
							{
								line.setText(lineText + " ) ) ");
								counts.remove(counts.size() - 1);
							} else
								line.setText(lineText + " ) ");
						}
						//System.out.println(counts);
						break;
					default:
						count = 0;
						String def = definitions.get(names.indexOf(text));
						for(String s : def.split(" "))
						{
							if(s.equals("X"))
								count++;
						}
						counts.add(count);

						line.setText(lineText + " " + text + "( ");

				}
			}else
			{
				switch(text)
				{
					case "=": //if button is "=" then do arithmetics
						String result = calculate(divide(lineText));
						//System.out.println(result + "dasdasd");
						line.setText(lineText + " = " + result);
						break;
					case "C": //if button is "C" then delete all expression
						line.setText("");
						counts.clear();
						break;
					case "DEL": //if button is "DEL" then delete the last symbol
						if(lineText.length() != 0)
						{
							int remove = 1;
							if(Character.toString(lineText.charAt(lineText.length()-1)).equals(" "))
								remove = 2;
							line.setText(lineText.substring(0,lineText.length()-remove));
						}
						break;
					case "+/-":
						line.setText(lineText + "-");
						break;
					default: //if button is any number or any action excepting "="
						if(lineText.indexOf("=") != -1)
						{
							line.setText(lineText.substring(lineText.indexOf("=") + 1, lineText.length()));
							lineText = line.getText();
						}
						if(tempList.contains(text))
						{
							if(lineText.length() != 0 && !Character.toString(lineText.charAt(lineText.length()-1)).equals(" "))
								line.setText(lineText + " ");
							if(Arrays.asList(priorityAr[0]).contains(text))
								line.setText(line.getText() + text + "( ");
							else
								line.setText(line.getText() + text + " ");
						}
						else
							line.setText(lineText + text);
				}
			}
		}
	}
	class Loader implements ActionListener
	{
		private File userActions; //file with actions of user

		public void actionPerformed(ActionEvent ae)
		{
			start();
		}

		public void start()
		{
			names.clear();
			namesOnly.clear();
			definitions.clear();

			JFileChooser fc = new JFileChooser("E:/All/java/my projects/Calculator/classes");
			FileNameExtensionFilter filt = new FileNameExtensionFilter("Text", "txt");
			fc.setFileFilter(filt);
			fc.showOpenDialog(frame);
			if((userActions = fc.getSelectedFile()) != null)
			{
				read();
				frame.setVisible(false);
				setFrame();
			}
		} 
		public void read()
		{
			try
			{
				BufferedReader br = new BufferedReader(new FileReader(userActions));
				String lineTxt;
				String[] tempAr;
				while((lineTxt = br.readLine()) != null)
				{
					tempAr = lineTxt.split(";");
					names.add(tempAr[0]);
					namesOnly.add(tempAr[0]);
					definitions.add(tempAr[1]);
				}
				names.add(">>>");
				//System.out.println(names);
				//System.out.println(definitions);
				//System.out.println(namesOnly);
				
			} catch(IOException e) {}
		}
	}
	class Creator implements ActionListener
	{
		String[] buttonsCr = {"X", "+", "-", "(", ")",
							  "*", "/", "%", "^",
							  "sqrt", "abs", "log10", "sin",
							  "cos", "fact",
							  "C", "DEL", "NEXT", "END & SAVE"};
		JTextField name;
		JTextField definition;
		ArrayList<String> lines = new ArrayList<String>();
		JFrame frameCr;
		public void actionPerformed(ActionEvent ae)
		{
			setGUI();
		}

		public void setGUI()
		{
			frameCr = new JFrame("Creator of operations' list");

			JPanel centerCr = new JPanel(new GridLayout(3, 5));

			JPanel fields = new JPanel(new GridLayout(1, 2));

			name = new JTextField();

			definition = new JTextField();
			definition.setEditable(false);

			fields.add(name);
			fields.add(definition);

			//creating buttons
			for(String s : buttonsCr)
			{
				JButton but = new JButton(s);
				but.addActionListener(new ButtonCrListener());
				centerCr.add(but);
			}
			
			//adding layout of the buttons and line of the expression to the screen
			frameCr.getContentPane().add(BorderLayout.CENTER, centerCr);
			frameCr.getContentPane().add(BorderLayout.NORTH, fields);

			frameCr.setSize(500,500);
			frameCr.setResizable(false);
			frameCr.setVisible(true);
		}
		class ButtonCrListener implements ActionListener
		{
			public void actionPerformed(ActionEvent ae)
			{
				JButton btn = (JButton) ae.getSource();

				String text = btn.getText();

				String defText = definition.getText();

				java.util.List tempList = Arrays.asList(actions);

				switch(text)
				{
					case "NEXT":
						if(!name.getText().equals(null) && !defText.equals(null))
						{
							lines.add(name.getText() + ";" + defText.trim());
							name.setText("");
							definition.setText("");
						}
						break;
					case "END & SAVE":
						if(!name.getText().equals(null) && !defText.equals(null))
						{
							lines.add(name.getText() + ";" + defText.trim());
							name.setText(""); 
							definition.setText("");
						} 

						try{
							JFileChooser fc = new JFileChooser("E:/all/java/my projects/Calculator/classes");
							FileNameExtensionFilter filt = new FileNameExtensionFilter("Text", "txt");
							fc.setFileFilter(filt);
							fc.showSaveDialog(frameCr);
							String file;
							BufferedWriter bw;
							if((file = fc.getSelectedFile().getName()).length() > 4 && (file = fc.getSelectedFile().getName()).substring(file.length() - 4,file.length()).equals(".txt"))
								bw = new BufferedWriter(new FileWriter(fc.getSelectedFile()));	
							else
								bw = new BufferedWriter(new FileWriter(fc.getSelectedFile() + ".txt"));

							for(String s : lines)
							{
								bw.write(s);
								bw.newLine();
							}
							bw.close();
						} catch(IOException e) {}
						frameCr.dispatchEvent(new WindowEvent(frameCr, WindowEvent.WINDOW_CLOSING));
						break;
					case "C": //if button is "C" then delete all expression
						definition.setText("");
						break;
					case "DEL": //if button is "DEL" then delete the last symbol
						if(defText.length() != 0)
						{
							int remove = 1;
							if(Character.toString(defText.charAt(defText.length()-1)).equals(" "))
								remove = 2;
							definition.setText(defText.substring(0,defText.length()-remove));
						}
						break;
					case "+/-":
						definition.setText(defText + "-");
						break;
					default:

						if(tempList.contains(text))
						{
							if(defText.length() != 0 && !Character.toString(defText.charAt(defText.length()-1)).equals(" "))
								definition.setText(defText + " ");
							if(Arrays.asList(priorityAr[0]).contains(text))
								definition.setText(definition.getText() + text + "( ");
							else
								definition.setText(definition.getText() + text + " ");
						}
						else
							definition.setText(defText + text);
				}
			}
		}
	}
}