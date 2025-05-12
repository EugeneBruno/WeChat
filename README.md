# 📬 Java GUI Chat App

A simple, multi-user chat application built in Java using Swing. It supports:

- Multiple clients across different devices (LAN)
- User-to-user private messaging
- GUI-based chat windows with separated conversations
- Username search and chat initiation
- Real-time message delivery

---

## 📁 Project Structure

```
JavaChatApp/
├── server/
│   ├── ChatServer.java
│   ├── ClientHandler.java
│   ├── User.java
│   └── UserRegistry.java
├── client/
│   ├── ChatClient.java
│   ├── LoginWindow.java
│   └── ChatWindow.java
└── README.md
```

---

## 🚀 How to Run

### 1. Compile All Java Files

```bash
javac server/*.java client/*.java
```

---

### 2. Run the Server

```bash
java server.ChatServer
```

You should see:

```
Chat server started...
```

---

### 3. Run the Client (GUI)

Open a new terminal (or a new machine on LAN):

```bash
java client.ChatClient
```

- Enter a **unique username**
- Use the search box to find another user
- Click **"Open Chat"** to begin messaging

Repeat this for as many clients as you want.

---

## 🌐 LAN Setup (Multi-Device)

1. On the host/server machine, get your local IP address:
   - Windows: `ipconfig`
   - Mac/Linux: `ifconfig` or `ip a`

2. In `ChatWindow.java`, change:

```java
Socket socket = new Socket("localhost", 12345);
```

to:

```java
Socket socket = new Socket("YOUR_LOCAL_IP_HERE", 12345);
```

3. Recompile the client and run it from other devices on the same LAN.

---

## ✨ Features

- 👥 Multiple users with unique usernames
- 🔐 One-on-one private messaging
- 🧠 Chats are separated using `CardLayout`
- 🔍 Username search to open new conversations
- 📤 Real-time message delivery over TCP sockets

---

## 🧩 Tech Stack

- Java SE 8+
- Swing (GUI)
- TCP Sockets

---

## 📌 Notes & Limitations

- No message encryption
- No persistent chat history (yet)
- Clients must be on the same LAN for cross-device chatting
- Server must be started first

---

## 📜 License

MIT License – feel free to use, modify, and share.
