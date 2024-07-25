package org.example.model.grammar;

public class SubroutineDec implements Node {
    private String type;
    private String returnType;
    private String subroutineName;

    public String getType() {
        return type;
    }

    public String getSubroutineName() {
        return subroutineName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void setSubroutineName(String subroutineName) {
        this.subroutineName = subroutineName;
    }
}
