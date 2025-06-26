export function list2Map<T> (list:T[],idName:string){
    const map = new Map<string, T>();
    for (let item of list) {
        map.set(item[idName], item);
    }
    return map
}
