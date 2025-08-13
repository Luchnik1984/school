package ru.hogwarts.school.exeption;

public class EntityAlreadyExistsException extends RuntimeException{
    public EntityAlreadyExistsException(String message){
        super (message);
    }
}
