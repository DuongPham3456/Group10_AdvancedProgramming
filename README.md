# Group10_AdvancedProgramming

Run code in terminal (lưu ổ E): 

cd "E:\prototype\prototype"
.\mvnw.cmd clean package
java -jar target\prototype-0.0.1-SNAPSHOT.jar

Open port at :

http://localhost:8080/

Cấu trúc thư mục: 

[Prototype]
 ├── pom.xml                               <-- (1) Thay thế/bổ sung thư viện vào file này
 │
 └── src
     └── main
         ├── java
         │   └── com
         │       └── tinthanh
         │           └── prototype         <-- (Thư mục package gốc)
         │               │
         │               ├── PrototypeApplication.java  <-- (File này Spring Initializr tự sinh, giữ nguyên)
         │               │
         │               ├── model                      
         │               │   └── ThietBiLyLich.java     <-- Đặt code Entity vào đây
         │               │
         │               ├── repository                
         │               │   └── ThietBiRepository.java <-- Đặt code Repository vào đây
         │               │
         │               └── controller                
         │                   └── ThietBiController.java <-- Đặt code Controller vào đây
         │
         └── resources
             ├── application.properties    <-- (5) Dán cấu hình H2 Database vào file này
             │
             └── static                    
                 └── index.html            <-- Đặt code HTML/JS giao diện vào đây
