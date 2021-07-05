package tech.pylons.ipc;

// Declare any non-default types here with import statements

interface IIpcInterface {

    String wallet2easel();

    void easel2wallet(String json);

    String getCoreData(String whatToGet);
}