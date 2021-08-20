# BloodBook (Project For Java Therap Fest)  

[YouTube Video Preview](https://youtu.be/KFeKdlUjyQU)

---

[![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2FMJKSabit%2FBloodBook&count_bg=%2379C83D&title_bg=%23555555&icon=darkreader.svg&icon_color=%23E7E7E7&title=Views&edge_flat=false)](https://hits.seeyoufarm.com)

> For connecting people with blood donors and bloodbanks who need blood.

BackEnd: [Heroku](https://blood-book.herokuapp.com) (Check if Maintenance mode is on, contact me \<sabit.jehadul.karim@gmail.com\>, if you want to test)

FrontEnd: [Netlify](https://blood-book.netlify.app)

## Used Technologies:

- Spring Boot [Backend]
- Spring Data JPA (Hibernate) [PostGRESQL]
- Spring Security [JWT]
- Spring Email [For Notification, Activation, Verification with GMail SMTP]
- Messenger Bot [Instant Notification]
- React [Frontend]
- Some external API (like geocoding)

## Features

### Auth / Registration / Activation

![](https://i.ibb.co/b5cCvnf/Blood-Book-3.png)

There are three types of user, General User as User, Bloodbank and Admin

### USER

User can see *all* or *personalized* **events** of bloodbanks and get notified for nearby events.

User can request for blood by **post** which will *notify* the users nearby via *email and messenger*. They can also find list of nearby bloodbanks where the user can find the requested blood. User can also see *all* or *personalized* **post**.

![](https://i.ibb.co/1L99Y6C/Blood-Book-5.png)

![](https://i.ibb.co/yBBr6P0/Blood-Book-4.png)

They can explore nearby bloodbank, update their info and connect to messenger or change password.

![](https://i.ibb.co/0cBndL9/Blood-Book-6.png)

## Blood Bank

BloodBanks can organize **events** and update their **blood stock** count or update their settings.

![](https://i.ibb.co/8M3ktsT/image.png)

## ADMIN

Admin can change user user access or other info. Also, can change password.

![](https://i.ibb.co/Zmt3DLq/image.png)
