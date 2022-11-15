@echo off
java -jar compiler.jar %1.luisa
g++ -o %1.exe %1.cpp
%1.exe