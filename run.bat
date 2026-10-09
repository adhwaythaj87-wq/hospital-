@echo off
title LifeCare Hospital Management System
echo ===================================================================
echo Starting LifeCare Hospital Management System (HMS)...
echo Web UI will be available at: http://localhost:8080
echo ===================================================================

if exist "%~dp0mvnw.cmd" (
    call "%~dp0mvnw.cmd" spring-boot:run
) else (
    call mvn spring-boot:run
)
pause
