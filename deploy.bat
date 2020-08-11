SET version=R-EAV-02

CALL :build Generator, generator
CALL :build Server, server

EXIT /B

:build
jpackage --name "WebDir - %~1" ^
--input webdir-%~2/target/jar ^
--main-jar webdir-%~2-%version%.jar ^
--dest deploy ^
--type app-image ^
--icon branding/Logo.ico ^
--copyright "Ktt Development 2020"