export interface GetCubeListForm {
    current: number;
    size:number;
}

export interface Cube {
    className: string;
    config: Config;
    description: string;
    extensionPackages: ExtensionPackage[];
    id: string;
    lastUpdatedTime: string;
    model: string;
    name: string;
    registeredTime: string;
    statistics: Statistics;
    status: string;
    version: string;
    [property: string]: any;
}

export interface Config {
    dependencies: string[];
    destroyMethod: null | string;
    initMethod: null | string;
    lazyInit: boolean;
    properties: null;
    singleton: boolean;
    [property: string]: any;
}

export interface ExtensionPackage {
    className: string;
    cubeId: string;
    description: string;
    extensionPointCount: number;
    extensionPoints: ExtensionPoint[];
    id: string;
    name: string;
    [property: string]: any;
}

export interface ExtensionPoint {
    asyncFlag: boolean;
    description: string;
    extensionName: string;
    id: string;
    methodName: string;
    parameterTypes: string[];
    pointResult: PointResult;
    priority: number;
    returnType: string;
    [property: string]: any;
}

export interface PointResult {
    description: string;
    required: boolean;
    resultType: string;
    [property: string]: any;
}

export interface Statistics {
    asyncExtensionPoints: number;
    averagePriority: number;
    memoryUsage: number;
    syncExtensionPoints: number;
    totalExtensionPackages: number;
    totalExtensionPoints: number;
    [property: string]: any;
}

export const statisticsItemName = {
    "totalExtensionPackages": "扩展包总数",
    "totalExtensionPoints": "扩展点总数",
    "syncExtensionPoints": "同步扩展点数",
    "asyncExtensionPoints": "异步扩展点数",
    "averagePriority": "平均优先级",
    "memoryUsage": "内存使用量",
}