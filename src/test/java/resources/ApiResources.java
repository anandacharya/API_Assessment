package resources;

public enum ApiResources {
    GetSimpleBooksAPI("books");
    private String resource;

    ApiResources(String resource){
        this.resource = resource;
    }

    public String getResource(){
        return resource;
    }
}
