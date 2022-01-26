import React from 'react';

export default function ChemItemHeader() {
    return (
        <div className="container">
            <div className="bg-white mb-2" style={{padding: "2px"}}>
                <div className="row" >
                    <div className="col-2">
                        <h4 className="mx-auto">chemical</h4>
                    </div>
                    <div className="col-sm-1">
                        <h4>quantity</h4>
                    </div>
                    <div className="col-1">
                        <h4 className="mx-auto">arrival</h4>
                    </div>
                    <div className="col-sm-2">
                        <h4>manufacturer</h4>
                    </div>
                    <div className="col-sm-2">
                        <h4>batch nr / seq nr</h4>
                    </div>
                    <div className="col-sm-2">
                    <h4>expration date</h4>
                    </div>
                    <div className="col-sm-2">
                        
                    </div>
                </div>
            </div>
        </div>
    )
}


