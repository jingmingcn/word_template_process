package io.infovis;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.rtf.*;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Process {
	
	enum State{
		Inital,
		Q,
		Block,
		A,
		B,
		C,
		D,
		ANS
		
	}
	
	public void run() {
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		chooser.showOpenDialog(null);
		File[] files = chooser.getSelectedFiles();
		for(File f : files) {
			
			try {
				RTFEditorKit rtfParser = new RTFEditorKit();
				Document document = rtfParser.createDefaultDocument();
				rtfParser.read(new FileInputStream(f), document, 0);
				String text = document.getText(0, document.getLength());
				String lines[] = text.split("\n");
				List<String> list = new ArrayList<String>();
				
				State state = State.Inital;
				int n = 1;
				for(String line :lines) {
					if(state == State.Inital) {
						if(line.trim().matches("^\\d+\\..*")) {
							line = line.replaceAll("^([\\d\\.\\sQ]+):", n+++"、");
							System.out.println(line);
							list.add(line);
							if(line.contains("{")) {
								state = State.Block;
							}else {
								state = State.Q;
							}
						}else {continue;}	
					}else if(state == State.Block) {
						if(line.trim().equals("}")) {
							state = State.Q;
						}
						System.out.println(line);
						list.add(line);
						continue;
					}else if(state == State.Q) {
						if(line.trim().matches("^[aA].*")) {
							line = line.replaceAll("^[aA]", "A");
							System.out.println(line);
							list.add(line);
							state = State.A;
						}else {
							String s = list.remove(list.size()-1);
							s += line;
							list.add(s);
							continue;
						}
						
					}else if(state == State.A) {
						if(line.trim().matches("^[bB].*")) {
							line = line.replaceAll("^[bB]", "B");
							System.out.println(line);
							list.add(line);
							state = State.B;
						}else {
							String s = list.remove(list.size()-1);
							s += line;
							list.add(s);
							continue;
						}
						
					}else if(state == State.B) {
						if(line.trim().matches("^[cC].*")) {
							
							line = line.replaceAll("^[cC]", "C");
							System.out.println(line);
							list.add(line);
							state = State.C;
						}else {
							String s = list.remove(list.size()-1);
							s += line;
							list.add(s);
							continue;
						}
						
					}else if(state == State.C) {
						if(line.trim().matches("^[dD].*")) {
							
							line = line.replaceAll("^[dD]", "D");
							System.out.println(line);
							list.add(line);
							state = State.D;
						}else {
							String s = list.remove(list.size()-1);
							s += line;
							list.add(s);
							continue;
						}
						
					}else if(state == State.D) {
						if(line.trim().matches("^[aA][nN][sS].*")) {
							Matcher m = Pattern.compile("^[aA][nN][sS]\\s*[:]*\\s*([aAbBcCdD]).*").matcher(line);
							line = m.replaceAll(r->"答案:"+r.group(1).toUpperCase());
							System.out.println(line);
							list.add(line);
							state = State.ANS;
						}else {
							String s = list.remove(list.size()-1);
							s += line;
							list.add(s);
							System.out.println(s);
							continue;
						}
						
					}else if(state == State.ANS) {
						line = "\r\n";
						System.out.println(line);
						list.add("难易程度：中");
						list.add("答案解析： ");
						list.add("题型：单选题");
						list.add(line);
						state = State.Inital;
					}
				}
				
				XWPFDocument doc = new XWPFDocument();
				XWPFParagraph p1 = doc.createParagraph();
				XWPFRun r1 = p1.createRun();
				for(String str: list) {
					r1.setText(str);
					r1.addCarriageReturn();
				}
				OutputStream os = new FileOutputStream(f.getAbsolutePath()+"_.docx");
				doc.write(os);
				doc.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Process process = new Process();
		process.run();
	}

}
