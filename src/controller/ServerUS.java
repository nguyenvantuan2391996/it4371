/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import entities.Data;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import model.Account;

/**
 *
 * @author tuannguyen
 */
public class ServerUS {

    public static ServerSocket serverUS;
    public static Socket socketUS;
    public static ObjectInputStream receiveDataClientUS;
    public static ObjectOutputStream sendDataClientUS;
    public static Account accountUS = new Account();
    public static Thread threadServerUS;
    public static Thread sendclientUS;

    public static Socket socket3;
    public static ObjectOutputStream sendDataServerVN;
    public static Thread sendServerVN;

    public static Socket socket4;
    public static ObjectOutputStream sendDataServerUK1;
    public static Thread sendServerUK1;

    public static boolean primaryUS;

    public void communicateClient() {
        threadServerUS = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    serverUS = new ServerSocket(8889);
                    System.out.println("Server is starting.........");

                    // chap nhan ket noi
                    socketUS = serverUS.accept();

                    System.out.println("Client US has been connected");

                    // nhận dữ liệu từ Client 
                    receiveDataClientUS = new ObjectInputStream(socketUS.getInputStream());
                    // gửi dữ liệu cho client
                    sendDataClientUS = new ObjectOutputStream(socketUS.getOutputStream());

                    while (true) {
                        Data dataReceive = (Data) receiveDataClientUS.readObject();
                        System.out.println("xu ly data tu client");
//                        Thread.sleep(5000);
                        Data dataSend = null;
                        if ("login".equals(dataReceive.getMessage())) {
                            dataSend = accountUS.checkLogin(dataReceive);
                            if (dataSend == null) {
                                dataSend = accountUS.checkLoginThePhu(dataReceive);
                            }
                            System.out.println("xu ly login");

                        } else if ("login admin".equals(dataReceive.getMessage())) {
                            dataSend = accountUS.checkLoginAdmin(dataReceive);
                            System.out.println("Xu ly login admin");

                        } else if ("check so du".equals(dataReceive.getMessage())) {
                            dataSend = accountUS.checkSoDu(dataReceive);
                            System.out.println("xu ly check so du");

                        } else if ("rut tien".equals(dataReceive.getMessage())) {
                            dataSend = accountUS.rutTien(dataReceive);
                            System.out.println("xu ly rut tien");

                        } else if ("doi mat khau".equals(dataReceive.getMessage())) {
                            dataSend = accountUS.doiMatKhau(dataReceive);
                            System.out.println("xu ly doi mat khau");

                        } else if ("xem chi tiet the".equals(dataReceive.getMessage())) {
                            dataSend = accountUS.getThongTinThe(dataReceive);
                            System.out.println("xu ly xem chi tiet the");

                        } else if ("nap tien the".equals(dataReceive.getMessage())) {
                            dataSend = accountUS.napTien(dataReceive);
                            System.out.println("xu ly nap tien the");

                        } else if ("tao the chinh".equals(dataReceive.getMessage())) {
                            dataSend = accountUS.taoTheChinh(dataReceive);
                            System.out.println("xu ly tao the chinh");

                        } else if ("tao the phu".equals(dataReceive.getMessage())) {
                            dataSend = accountUS.taoThePhu(dataReceive);
                            System.out.println("xu ly tao the phu");

                        } else if ("xoa the".equals(dataReceive.getMessage())) {
                            dataSend = accountUS.xoaThe(dataReceive);
                            System.out.println("xu ly tao xoa the");
                        }
                        
                        if ("primary".equals(dataReceive.getPrimary())) {
                            primaryUS = true;
                            dataReceive.setPrimary(null);
                            connectServerUK();
                            connectServerVN();
                        }
                        if (primaryUS) {
                            sendClient(dataSend);
                            sendServerUK(dataReceive);
                            
                        }

                    }
                } catch (Exception e) {
                }
            }
        });

        threadServerUS.start();
    }

    public void sendClient(Data dataSend) {
        sendclientUS = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    sendDataClientUS.writeObject(dataSend);
                    System.out.println("gui data cho client");
//                    Thread.sleep(5000);
                    sendDataClientUS.flush();
                } catch (Exception e) {
                }
            }
        });
        sendclientUS.start();
    }

    public void connectServerVN() {
        try {
            socket3 = new Socket("localhost", 8888);

            System.out.println("Connecting to server VN ....... \n");

            // gửi dữ liệu cho server VN
            sendDataServerVN = new ObjectOutputStream(socket3.getOutputStream());

            System.out.println("Connect Server VN success .......\n");
        } catch (Exception e) {
        }
    }

    public void sendServerVN(Data data) {
        sendServerVN = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    
                    sendDataServerVN.writeObject(data);
                    System.out.println("gui data cho Server VN");
                    sendDataServerVN.flush();

                } catch (Exception e) {
                }
            }
        });

        sendServerVN.start();
    }

    public void connectServerUK() {
        try {
            socket4 = new Socket("localhost", 8890);

            System.out.println("Connecting to server UK ....... \n");

            // gửi dữ liệu cho server
            sendDataServerUK1 = new ObjectOutputStream(socket4.getOutputStream());

            System.out.println("Connect Server UK success .......\n");
        } catch (Exception e) {
        }
    }

    public void sendServerUK(Data data) {
        sendServerUK1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    data.setLocation("bank_uk");
                    sendDataServerUK1.writeObject(data);
                    System.out.println("gui data cho Server UK");
                    sendDataServerUK1.flush();

                    data.setLocation("bank_vn");
                    sendServerVN(data);
                    
                } catch (Exception e) {
                }
            }
        });

        sendServerUK1.start();
    }

    public static void main(String[] args) {
        ServerUS us = new ServerUS();
        us.communicateClient();
    }
}
