package ru.pricechecker.main;

import java.util.Map;

import ru.pricechecker.main.FileReader.LoadData;

public class Main {

	public static void main(String[] args) {

		if(FileReader.readFile("D:\\123.txt")){
			TCPServer tcpServer = new TCPServer(2004);
			if(tcpServer.startServer()){
				System.out.println("��������� ���������...");
				tcpServer.startRequestHandle();
			} else
				System.out.println("�� ������ ������� ����!");
		} else 
			System.out.println("�� ������ ��������� ����!");
	}
}
