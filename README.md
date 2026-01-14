# Java Discussion Forum

A Java-based discussion forum application with role-based access control.  
This project was built on top of a starter template included in the `original-templates/` directory (see **Template Attribution** below).

---

## Features

- **H2 Database Integration**  
  - Stores users, posts, threads, grades, invitation codes, and admin requests for persistent data management.

- **Entities & Class Design**  
  - `Post` entity represents forum posts  
  - `Reply` entity as a subclass of `Post`  
  - `User` entity stores user details and roles  

- **Role-Based Access Control**  
  - **Multiple roles per user:** Users with multiple roles can choose which role to use at login  
  - **Admins** have full control over users, roles, and requests  

- **User Management (Admin Only)**
  - Add or remove roles from existing users  
  - Delete users 
  - Special setup for the first user of the application 
  - Invite new users  

- **Home Pages**  
  - Customized home pages for each user type (Admin, Staff, Student)  

- **Posts & Threads Management (CRUD)**  
  - Create, read, update, delete posts:  
    - **Admin:** full CRUD access  
    - **Staff:** can edit their own posts, delete any post  
    - **Student:** can edit or delete only their own posts  
  - Create threads for posts (**Admin and Staff only**)  
  - Search posts by keywords  

- **Grades Management**  
  - CRUD for grades:  
    - **Admin/Staff:** view and manage all grades  
    - **Student:** view only their own grades  

- **Username/Password Validation**  
  - Validates characters, length, and other security requirements
 
- **One-Time Passwords**  
  - Admin users can generate a one-time password for other users
  - This password expires after 24 hours 

- **Admin Requests**  
  - Staff users can create requests for admins  
  - Admins can fulfill and close requests  
  - Staff can reopen requests with a new description if needed  

---

## Template Attribution

This project was developed using a starter template provided by Zahra Sadri-Moshkenani @ Arizona State University.  
The original template source code is included in the `original-templates/` directory.  
It is included with explicit permission from professor Zahra Sadri-Moshkenani to share publicly on GitHub.  

---

## Technology Stack

- Java (object-oriented design)  
- JavaFX (graphical user interface)
- H2 Database (local storage)   
- Eclipse IDE (project setup)  
