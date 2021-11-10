export type Constructor = new (...args: any[]) => {}

export type TConstructor<T> = new (...args: any[]) => T;
