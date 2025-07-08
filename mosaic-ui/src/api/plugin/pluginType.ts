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
    scope: String;
    name: string;
    registeredTime: string;
    statistics: Statistics;
    status: string;
    version: string;
    [property: string]: any;
}

export interface Config {
    configInfo?: ConfigInfo | null;
    dependencies?: string[];
    destroyMethod?: string | null;
    initMethod?: string | null;
    lazyInit?: boolean;
    properties?: any;
    singleton?: boolean;
    [property: string]: any;
}


export interface ConfigItem {
    name: string;
    type: 'string' | 'integer' | 'double' | 'boolean' | 'array' | 'object';
    desc: string;
    required: boolean;
    defaultValue: any;
    validation?: {
        allowedValues?: string[];
        minValue?: number;
        maxValue?: number;
        pattern?: string;
        [key: string]: any;
    };
}

export interface ConfigInfo {
    id: string;
    config: ConfigItem[];
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
    pointItems: PointResultItem[];
    [property: string]: any;
}

export interface PointResultItem {
    itemName: string;
    itemClass: string;
    description: string;
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