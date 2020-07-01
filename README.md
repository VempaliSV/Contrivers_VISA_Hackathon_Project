# **Contrivers VISA Hackathon Project**



## Overview
Our idea thrives to promote cashless payments by integrating the usage of E-wallets and VISA payment services.We are providing an Open-Loop transaction system for e-Wallets which are usually a part of the Close-Loop transaction system by leveraging Visa APIs. 
In the present case scenario, e-Wallets are not admissible in all market segments but with the help of One Visa Connect customers would be able to have a seamless & secure virtual payment experience by being a part of Visa’s global network.

 With the incredibly rapid development of digital marketing, a need for a more secure and stable method of receiving payments online among retail merchants and stores everywhere has risen .
Online merchants and service sellers utilise our solution of a Virtual visa POS Terminal(One Visa Connect). It is a payment gateway which allows them to manually authorise card transactions initiated by the buyer.
This process greatly expands their payment sources and reduces the time of the payment process, while adding additional security.

![APP LAYOUT](https://github.com/VempaliSV/Contrivers_VISA_Hackathon_Project/ONEVISACONNECT/Customer/AndroidApp/Web1920–1.jpg?raw=true)



## Tech

* *JAVA* - Java programing language used for android development
* *Material Design* - Material is a design system – backed by open-source code – that helps teams build high-quality digital experiences.
* *Visa ParserSDK-3.2.0* - SDK for use by an Issuer for mobile app development.
* *Visa GeneratorSDK-3.2.0* - Mobile Push Payment Acquirer/Merchant SDK for QR generation.
* *zxing* - barcode image processing library implemented in Java, with ports to other languages. 
* *Flask* - Flask is a micro web framework written in Python.
* *SQLAlchemy* - SQLAlchemy is an open-source SQL toolkit and object-relational mapper for the Python programming language.
* *SQLite/Postgres/MySQL*- Relational database management system emphasizing extensibility and SQL compliance. 
---
## How to get Started
### Installation

Install the dependencies and devDependencies and start the server.

```sh
pip install -r requirements.txt
```
#### Android Dependecies
##### Build Gradle Requirments
Android Studio Version 4.0\
Minimum SDK version 21\
Target SDK version 28\
Compile SDK version 28\
Version Code 1

#### Customer Application
```sh
4-Digit PIN default 1234
6-Digit OTP default 123456
```

### Development
---
#### Customer API

**Installation**

```sh
pip install -r requirements.txt
```
**Default Port**
```sh
default port http://127.0.0.1:5001/
```

**.env.example** \
Set according to your requirements \
APP_SECRET_KEY\
DATABASE_URI\
USERNAME_FOR_VISA_API\
PASSWORD_FOR_VISA_API

 **wallet.py**
 ```sh
default port http://127.0.0.1:5000/
```
 port = Set according to your configured port

**visaAPI.py**
```sh
url = "Enter MVisa Merchant Push Payment URL"
```

**Create a folder VisaCert in main directory**\
Insert \
Project Certificate as cert.pem \
Private Key as privateKey.pem \
SSL Certificate as server.pem

**MLE Directory**
Insert\
RSA Public Key as public.pem\
RSA Private Key as private.pem

#### Merchant API

**Installation**

```sh
pip install -r requirements.txt
```
**Default Port**
```sh
default port http://127.0.0.1:5002/
```

**.env.example**\
Set according to your requirements \
APP_SECRET_KEY \
DATABASE_URI\
USERNAME_FOR_VISA_API\
PASSWORD_FOR_VISA_API

 **visaNet.py**
 ```sh
default port http://127.0.0.1:5001/
```
 port = Set according to your configured port

**pullFunds.py**
```sh
url = "Enter Funds Transfer Pull Payment URL"
```

**Create a folder VisaCert in main directory**\
Insert \
Project Certificate as cert.pem \
Private Key as privateKey.pem \
SSL Certificate as server.pem 

#### Wallet API

**Installation**

```sh
pip install -r requirements.txt
```
**Default Port**
```sh
default port http://127.0.0.1:5000/
```

**.env.example**\
Set according to your requirements \
WALLET_KEY generate via fernet \
DATABASE_URI \
SECRET_KEY

#### Customer Application
**app/src/main/assets**
```sh
Insert
RSA Public Key as public.pem
RSA Private Key as private.pem
```
**Login Activity**
```sh
login
requestLogin.execute(Enter Server Port of Customer API)
onCreate
requestRefresh.execute(Enter Server Port of Customer API)
```
**Register Activity**

```sh
register
requestRegister.execute(Enter Server Port of Customer API)
```

**OTP Activity**
```sh
Send OTP
sendOtp.execute(Enter Server Port of Customer API)
Verify OTP
verifyOTP.execute(Enter Server Port of Customer API)
```
**Home Activity**
```sh
logout
requestLogout.execute(Enter Server Port of Customer API)
```
**HomeActivityFragments/Wallets/FragmentWallets**
```sh
onCreateView
getWalletAmount.execute(Enter Server Port of Customer API)
```
**HomeActivityFragments/History/FragmentHistory**
```sh
onCreateView
requestHistory.execute(Enter Server Port of Customer API)
```
**API/CheckCard**
```sh
onWalletClick
checkCard.execute(Enter Server Port of Customer API)
```
**API/GenerateCard**
```sh
onPostExecute
generateCard.execute(Enter Server Port of Customer API)
```
**GenrateQRActivity**
```sh
OnCreate
getPan.execute(Enter Server Port of Customer API)
```
**PaymentResultActivity**
```sh
OnCreate
requestPayment.execute(Enter Server Port of Customer API)
```

### Merchant Application
**Login Activity**
```sh
Login
requestLogin.execute(Enter Server Port of Customer API)
onCreate
requestRefresh.execute(Enter Server Port of Customer API)
```
**Register Activity**
```sh
Register
requestRegister.execute(Enter Server Port of Customer API)
```
**Home Activity**
```sh
Logout
requestLogout.execute(Enter Server Port of Customer API)
```
**Home Activity**
```sh
onCreate
requestHistory.execute(Enter Server Port of Customer API)
```
**Payment Result Activity**
```sh
onCreate
requestPayment.execute(Enter Server Port of Customer API)
```




![](https://www.visa.co.in/content/dam/VCOM/Brand/logo-footer.png)
