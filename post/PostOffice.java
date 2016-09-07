package post;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.*;

import javax.transaction.xa.Xid;

public class PostOffice {

	static int NUMBER_OF_BOXES = 25;
	static ArchiveType ARCHIVE_TYPE = ArchiveType.FILE;
	enum ArchiveType {FILE, DB}
	
	private HashMap<Integer, ArrayList<PostObject>> boxes = new HashMap();
	private ArrayList<PostObject> hranilishte = new ArrayList<>();
	private TreeMap<LocalDate, TreeMap<LocalTime,PostObject>> archive = new TreeMap<LocalDate, TreeMap<LocalTime, PostObject>>(new Comparator<LocalDate>() {
		
		@Override
		public int compare(LocalDate o1, LocalDate o2) {
			if(o1.isBefore(o2)){
				return -1;
			}
			if(o1.isAfter(o2)){
				return 1;
			}
			return 0;
		};
	});
	private HashSet<Gatherer> gatherers = new HashSet();
	private HashSet<Postman> postmen = new HashSet();
	
	public PostOffice() {
		postmen.add(new Postman("Bella", "Belova", "Belovo", 3));
		postmen.add(new Postman("Alex", "Alexisova", "Alexandrovo", 6));
		postmen.add(new Postman("David", "Davidov", "Davidovo", 23));
		
		gatherers.add(new Gatherer("Lili", "Lileva", "Lilevo"));
		gatherers.add(new Gatherer("Tedi", "Tedeva", "Kude si tedi?"));
		gatherers.add(new Gatherer("Mimi", "Mimeva", "Mariichevo"));
		gatherers.add(new Gatherer("Katya", "Kateva", "Kateri4ino"));
		
		for(int i = 0; i < NUMBER_OF_BOXES; i++){
			boxes.put(i, new ArrayList<>());
		}
		
		Archiver archiver = new Archiver();
		archiver.setDaemon(true);
		archiver.start();
	}
	
	public synchronized void postLetterInBox(int box, Letter letter) {
		boxes.get(box).add(letter);
	}

	public synchronized void post(PostObject obj) {
		LocalDate now = LocalDate.now();
		if(!archive.containsKey(now)){
			archive.put(now, new TreeMap<LocalTime, PostObject>(new Comparator<LocalTime>() {

				@Override
				public int compare(LocalTime o1, LocalTime o2) {
					if(o1.isBefore(o2)){
						return -1;
					}
					if(o1.isAfter(o2)){
						return 1;
					}
					return 0;
				}
			}));
		}
		archive.get(now).put(LocalTime.now(), obj);
		if(!hranilishte.contains(obj))
			hranilishte.add(obj);

		System.out.println("PostObject posted in hranilishet and arvhive ");
	}

	public void startWork(){
		long start = System.currentTimeMillis();
		while(System.currentTimeMillis() - start < 120*1000){
			if(hranilishte.size() < 50){
				System.out.println("hranilishte has " + hranilishte.size() + " pratki - po4vame da subirame");
				gather();
				System.out.println("gather ended");
			}
			else{
				System.out.println("hranilishte has " + hranilishte.size() + " pratki - po4vame da razdavame");
				deliver();
			}
		}
		File reports = new File("reports.txt");
		try {
			reports.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		PrintWriter pr = null;
		try {
			pr = new PrintWriter(reports);
			System.out.println("=================");
			printPratki(LocalDate.now(), pr);
			System.out.println("=================");
			printLetterPecentage(LocalDate.now(), pr);
			System.out.println("=================");
			printFragilePercentage(pr);
			System.out.println("=================");
			printPostmenWork(pr);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			pr.close();
		}
	}

	private void deliver() {
		Set<Postman> freePostmen = new HashSet();
		for(Postman p : postmen){
			if(p.isFree()){
				freePostmen.add(p);
			}
		}
		if(freePostmen.isEmpty()){
			return;
		}
		int pratkiZaRazdavane = hranilishte.size()/freePostmen.size();
		
		for(Postman p : freePostmen){
			ArrayList<PostObject> pratkiNaToziPostman = new ArrayList<>();
			for(int i = 0; i < pratkiZaRazdavane; i++){
				pratkiNaToziPostman.add(hranilishte.remove(hranilishte.size()-1));
			}
			p.razdai(pratkiNaToziPostman);
		}
	}

	private void gather() {
		int box = 0;//do 25
		ArrayList<PostObject> allGatheredObjs = new ArrayList<>();
		while(box < NUMBER_OF_BOXES){
			for(Gatherer g : gatherers){
				int boxNumber = box;
				if(boxNumber >= NUMBER_OF_BOXES){
					break;
				}
				allGatheredObjs.addAll(boxes.get(boxNumber));
				boxes.get(boxNumber).clear();
				box++;
			}
		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(PostObject po : allGatheredObjs){
			post(po);
		}
	}
	
	public void printPratki(LocalDate date, PrintWriter pr){
		TreeMap<LocalTime,PostObject> pratkiZaData = archive.get(date);
		for(Map.Entry<LocalTime,PostObject> pratka : pratkiZaData.entrySet()){
			pr.println(pratka.getKey() + " - " + pratka.getValue());
		}
	}
	

	
	public void printLetterPecentage(LocalDate date, PrintWriter pr){
		TreeMap<LocalTime,PostObject> pratkiZaData = archive.get(date);
		double letters = 0;
		for(PostObject obj : pratkiZaData.values()){
			if(obj.isLetter()){
				letters+=1;
			}
		}
		pr.println("Letters % = " + ((letters/pratkiZaData.size())*100));
	}
	
	public void printFragilePercentage(PrintWriter pr){
		double fragile = 0;
		double koleti = 0;
		for(TreeMap<LocalTime,PostObject> pratkiZaDen : archive.values()){
			for(PostObject pratka : pratkiZaDen.values()){
				if(!pratka.isLetter()){
					koleti+=1;
				}
				if(pratka.isFragile()){
					fragile+=1;
				}
			}
		}
		pr.println("Fragile % = " + ((fragile/koleti)*100) );
	}
	
	public void printPostmenWork(PrintWriter pr){
		TreeSet<Postman> postmenByWork = new TreeSet<Postman>(new Comparator<Postman>() {

			@Override
			public int compare(Postman o1, Postman o2) {
				if(o1.getRazprateni() == o2.getRazprateni()){
					return o1.getName().compareTo(o2.getName());
				}
				return o1.getRazprateni() - o2.getRazprateni();
			}
		});
		postmenByWork.addAll(postmen);
		for(Postman p : postmenByWork){
			pr.println(p.getCitizenName() + " - " + p.getRazprateni());
		}
	}

	private class Archiver extends Thread{
		
		@Override
		public void run() {
			while(true){
				try {
					Thread.sleep(25000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(ARCHIVE_TYPE == ArchiveType.FILE){
					File archiveFile = new File("backup-"+LocalTime.now()+".txt");
					
					try(PrintWriter pr = new PrintWriter(archiveFile);){
						archiveFile.createNewFile();
						TreeMap<LocalTime,PostObject> pratkiZaData = archive.get(LocalDate.now());
						if(pratkiZaData == null){
							return;
						}
						for(Map.Entry<LocalTime,PostObject> entry : pratkiZaData.entrySet()){
							PostObject pratka = entry.getValue();
							pr.println(pratka.toString() + " FROM " + pratka.getSenderName() + " TO " + pratka.getReceiverName());
						}
					}
					catch(IOException e){
						
					}
					
				}
				else
				if(ARCHIVE_TYPE == ArchiveType.DB){
					try {
						Class.forName("com.mysql.jdbc.Driver");
						Connection con = DriverManager.getConnection("jdbc:mysql://192.168.8.22:3306/hr", "ittstudent","ittstudent-123");
						
						TreeMap<LocalTime,PostObject> pratkiZaData = archive.get(LocalDate.now());
						for(Map.Entry<LocalTime,PostObject> entry : pratkiZaData.entrySet()){
							PostObject pratka = entry.getValue();

							PreparedStatement statement = con.prepareStatement("INSERT INTO post_office_archive (pratka, sender_name, receiver_name, date_of_sending) VALUES (?, ?, ?, ?)");
							statement.setString(1, pratka.toString());
							statement.setString(2, pratka.getSenderName());
							statement.setString(3, pratka.getReceiverName());
							statement.setString(4, entry.getKey().toString());
							
						}
					
					
					} catch (SQLException | ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
}
