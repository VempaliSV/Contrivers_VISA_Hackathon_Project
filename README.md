# **Contrivers VISA Hackathon Project**


### Overview
Our idea thrives to promote cashless payments by integrating the usage of E-wallets and VISA payment services.We are providing an Open-Loop transaction system for e-Wallets which are usually a part of the Close-Loop transaction system by leveraging Visa APIs. 
In the present case scenario, e-Wallets are not admissible in all market segments but with the help of One Visa Connect customers would be able to have a seamless & secure virtual payment experience by being a part of Visa’s global network.

 With the incredibly rapid development of digital marketing, a need for a more secure and stable method of receiving payments online among retail merchants and stores everywhere has risen .
Online merchants and service sellers utilise our solution of a Virtual visa POS Terminal(One Visa Connect). It is a payment gateway which allows them to manually authorise card transactions initiated by the buyer.
This process greatly expands their payment sources and reduces the time of the payment process, while adding additional security.



### Tech

* *JAVA* - 
* *Material UI* - 
* *VisaParserSDK-3.2.0* - 
* *Visa GeneratorSDK-3.2.0* - 
* *zxing* - 
* *Flask* -
* *SQLAlchemy* - 
* *SQLite/Postgres/MySQL*-
### Installation

Install the dependencies and devDependencies and start the server.

```sh
pip install -r requirements.txt
```
#### Android Dependecies
**Build Gradle Requirments**\
Minimum SDK version 21 \
Android Studio Version 5.0\
Target SDK version 28\
Compile SDK version 28\
Version Code 1

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
APP_SECRET_KEY =\
DATABASE_URI=\
USERNAME_FOR_VISA_API=\
PASSWORD_FOR_VISA_API=

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
APP_SECRET_KEY =  \
DATABASE_URI=\
USERNAME_FOR_VISA_API=\
PASSWORD_FOR_VISA_API=

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
DATABASE_URI= \
SECRET_KEY= 
