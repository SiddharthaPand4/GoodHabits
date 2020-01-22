CALL yarn build
CALL del /s /q ..\..\resources\public\*.*
CALL copy /y build\index.html ..\..\resources\templates\index.ftl
CALL xcopy /E /Y  build ..\..\resources\public\

