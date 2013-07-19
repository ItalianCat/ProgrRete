cd javarmi/pharma
javac *.java
cd ..
rmic -d ~/public_html/common/ pharma.Server
rmic -d ~/public_html/common/ pharma.ServAutenticazione
rmic -d ~/public_html/common/ pharma.ServProxy
rmic -iiop -d ~/public_html/common/ pharma.ServProxy
rmic -d ~/public_html/common/ pharma.ServBootstrap
rmic -iiop -d ~/public_html/common/ pharma.ServBootstrap

cp /home/accounts/studenti/id585uee/javarmi/pharma/*.class /home/accounts/studenti/id585uee/public_html/common/pharma/
