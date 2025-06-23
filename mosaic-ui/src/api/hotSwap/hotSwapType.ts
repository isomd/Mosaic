export interface CreatePointForm {
    cubeId: string | null;
    exPackageId: string | null;
    exPointId: string | null;
    resName: string | null;
    slotId: string;

    className:string;
    targetLine:number;
    changeType:string;
    "args":string[];
    [property: string]: any;
}
