CALL yarn build
CALL copy /y build\index.html ..\..\resources\templates\index.ftl
CALL xcopy /E /Y  build ..\..\resources\public\
