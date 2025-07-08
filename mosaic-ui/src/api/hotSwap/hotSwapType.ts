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

export interface GetHotSwapPointsForm {
    className: string;
}

export interface RollBackHotSwapPointForm {
    className: string;
    method: string;
}

export interface PointVersion {
    versionId: string;
    createAt: string;
    className: string;
    changeType: string;
    changeRecord: ChangeRecord;
}
export interface ChangeRecord {
    methodName: string;
    oldSourceCode: string;
    newSourceCode: string;
}
export interface MethodInfo {
    className:string;
    startLineNumber: number;
    endLineNumber: number;
    differentLineNumber:number;
    methodName: string;
    newSourceCode:string;
    oldSourceCode: string;
}
