@echo off
setlocal

set base_dir=D:\Scheduler
set enoviaTitleUpdate=%base_dir%\EnoviaTitleUpdate

md %enoviaTitleUpdate%

icacls %base_dir% /t  /grant Everyone:F

pause

