package ru.hogwarts.school.ecxeption;

public class EntityAlreadyExistsException extends RuntimeException{
    public EntityAlreadyExistsException(String message){
        super (message);
    }
}
