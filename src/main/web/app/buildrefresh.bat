yarn build
copy /y build\index.html ..\..\resources\templates\index.ftl
xcopy /E /Y  build ..\..\resources\public\
