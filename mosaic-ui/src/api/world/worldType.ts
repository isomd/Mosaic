export interface CreateWorldForm {
    name:String;
}
export interface World {
    id:{
        uuid:string
    }
    name:String;
    version:number
}
export interface TraverseWorldForm{
    uuid:string;
}
export interface DeleteWorldForm{
    uuid:string;
}
