package com.example.firstproject.compactdrive;

class GfileObject {
    private String id=null;
    private String title=null;
    private String owner=null;
    private String dateOfCreation = null;
    private String mimeType =null;
    public String getID(){
        return id;
    }
    public void setID(String id){
        this.id=id;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getOwner(){
        return owner;
    }
    public void setOwner(String owner){
        this.owner = owner;
    }
    public String getDoc(){
        return dateOfCreation;
    }
    public void setDoc(String doc){
        this.dateOfCreation=doc;
    }
    public String getMimeType(){
        return mimeType;
    }
    public void setMimeType(String mime){
        this.mimeType=mime;
    }
}