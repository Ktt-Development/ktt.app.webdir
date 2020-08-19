SET version=01.00.00

CALL :build Generator, generator
CALL :build Server, server

EXIT /B

:build

copy "LICENSE." "webdir-%~2/target/jar"
copy "licenses.txt" "webdir-%~2/target/jar"

jpackage --name "WebDir - %~1" ^
--input webdir-%~2/target/jar ^
--main-jar webdir-%~2-%version%.jar ^
--dest deploy ^
--type app-image ^
--icon branding/Logo.ico ^
--copyright "Ktt Development 2020"