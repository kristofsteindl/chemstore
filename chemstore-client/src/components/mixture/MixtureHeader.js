import React from 'react';

export default function MixtureHeader() {
    return (
        <div className="bg-white mb-2 row" style={{padding: "2px"}}>
            <div className="col-sm-1">
                <h4>id</h4>
            </div>
            <div className="col-3">
                <h4 className="mx-auto">name</h4>
            </div>
            <div className="col-sm-2">
                <h4>project</h4>
            </div>
            <div className="col-1">
                <h4 className="mx-auto">amount</h4>
            </div>
            <div className="col-sm-2">
                <h4>created</h4>
            </div>
            <div className="col-sm-1">
                <h4>expire</h4>
            </div>
            <div className="col-sm-2" />
        </div>
    )
}


