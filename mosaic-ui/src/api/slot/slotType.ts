export interface CreateSlotForm {
    cubeId: string | null;
    exPackageId: string | null;
    exPointId: string | null;
    resName: string | null;
    setupFlag: boolean;
    slotId: string;
    [property: string]: any;
}
export interface Slot {
    cubeId: string | null;
    exPackageId: string | null;
    exPointId: string | null;
    resName: string | null;
    setupFlag: boolean;
    slotId: string;
    [property: string]: any;
}
